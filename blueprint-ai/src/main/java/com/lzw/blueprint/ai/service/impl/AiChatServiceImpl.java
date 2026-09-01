package com.lzw.blueprint.ai.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lzw.blueprint.ai.dto.ChatResponseDto;
import com.lzw.blueprint.ai.dto.ModelInfoDto;
import com.lzw.blueprint.ai.entity.AiProvider;
import com.lzw.blueprint.ai.entity.ChatMessage;
import com.lzw.blueprint.ai.entity.ChatSession;
import com.lzw.blueprint.ai.service.AiChatService;
import com.lzw.blueprint.ai.provider.ProviderRouter;
import com.lzw.blueprint.ai.service.audit.AiUsageLimitService;
import com.lzw.blueprint.ai.service.chat.ChatMessageService;
import com.lzw.blueprint.ai.service.chat.ChatSessionService;
import com.lzw.blueprint.ai.event.AiUsageEvent;
import com.lzw.blueprint.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AiChatServiceImpl implements AiChatService {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final double DEFAULT_TEMPERATURE = 0.7;

    private final ProviderRouter providerRouter;
    private final ChatSessionService chatSessionService;
    private final ChatMessageService chatMessageService;
    private final AiUsageLimitService aiUsageLimitService;
    private final ApplicationEventPublisher eventPublisher;

    public AiChatServiceImpl(ProviderRouter providerRouter,
                             ChatSessionService chatSessionService,
                             ChatMessageService chatMessageService,
                             AiUsageLimitService aiUsageLimitService,
                             ApplicationEventPublisher eventPublisher) {
        this.providerRouter = providerRouter;
        this.chatSessionService = chatSessionService;
        this.chatMessageService = chatMessageService;
        this.aiUsageLimitService = aiUsageLimitService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public ChatResponseDto chat(Long userId, String sessionId, String content) {
        aiUsageLimitService.checkLimit(userId);

        // ponytail: session auto-created on first message, title from first user message
        ChatSession session = resolveSession(userId, sessionId, content);
        Long sid = session.getId();

        String reply = callProvider(session, content);

        saveUserMessage(sid, content, null);
        saveAssistantMessage(sid, reply, null, 0, 0, "stop");

        publishUsage(userId, content, reply);

        ChatResponseDto dto = new ChatResponseDto();
        dto.setSessionId(sid);
        dto.setReply(reply);
        return dto;
    }

    @Override
    public Flux<String> chatStream(Long userId, String sessionId, String content) {
        // ponytail: stream wraps the blocking call; real streaming requires async HTTP client
        ChatResponseDto response = chat(userId, sessionId, content);
        return Flux.just(response.getReply());
    }

    @Override
    public List<ModelInfoDto> listModels() {
        List<AiProvider> providers = providerRouter.getEnabledProviders();
        return providers.stream()
                .filter(p -> p.getDefaultModel() != null && !p.getDefaultModel().isBlank())
                .map(p -> {
                    ModelInfoDto dto = new ModelInfoDto();
                    dto.setModelId(p.getDefaultModel());
                    dto.setProviderCode(p.getProviderCode());
                    return dto;
                })
                .distinct()
                .collect(Collectors.toList());
    }

    private ChatSession resolveSession(Long userId, String sessionId, String content) {
        if (sessionId != null && !sessionId.isBlank()) {
            try {
                ChatSession existing = chatSessionService.getById(Long.valueOf(sessionId));
                if (existing != null) return existing;
            } catch (NumberFormatException ignored) {
            }
        }
        return chatSessionService.create(userId, truncateTitle(content), null);
    }

    private void saveUserMessage(Long sessionId, String content, String model) {
        ChatMessage msg = new ChatMessage();
        msg.setSessionId(sessionId);
        msg.setRole("user");
        msg.setContent(content);
        msg.setModel(model);
        chatMessageService.save(msg);
    }

    private void saveAssistantMessage(Long sessionId, String content, String model, int tokensIn, int tokensOut, String finishReason) {
        ChatMessage msg = new ChatMessage();
        msg.setSessionId(sessionId);
        msg.setRole("assistant");
        msg.setContent(content);
        msg.setModel(model);
        msg.setTokensInput(tokensIn);
        msg.setTokensOutput(tokensOut);
        msg.setFinishReason(finishReason);
        chatMessageService.save(msg);
    }

    private String truncateTitle(String content) {
        return content.length() <= 50 ? content : content.substring(0, 50) + "...";
    }

    private void publishUsage(Long userId, String content, String reply) {
        if (userId == null) {
            return;
        }
        AiProvider provider = providerRouter.resolveProvider(null);
        AiUsageEvent event = new AiUsageEvent();
        event.setUserId(userId);
        event.setProviderCode(provider != null ? provider.getProviderCode() : "offline");
        event.setModelId(provider != null ? provider.getDefaultModel() : null);
        // ponytail: rough ~4 chars/token estimate; real counting needs tokenizer
        event.setTokensInput(content != null ? Math.max(1, content.length() / 4) : 0);
        event.setTokensOutput(reply != null ? Math.max(1, reply.length() / 4) : 0);
        event.setRequestCount(1);
        eventPublisher.publishEvent(event);
    }

    private String callProvider(ChatSession session, String userContent) {
        AiProvider provider = providerRouter.resolveProvider(null);
        if (provider == null || provider.getApiKey() == null || provider.getApiKey().isBlank()) {
            return "[offline] No AI provider configured";
        }
        try {
            String base = provider.getBaseUrl();
            if (base == null || base.isBlank()) {
                base = "https://api.openai.com";
            }
            String endpoint = base.endsWith("/") ? base + "v1/chat/completions" : base + "/v1/chat/completions";

            ObjectNode root = MAPPER.createObjectNode();
            String model = provider.getDefaultModel();
            if (model == null || model.isBlank()) {
                model = "gpt-3.5-turbo";
            }
            root.put("model", model);
            root.put("temperature", session.getTemperature() != null ? session.getTemperature().doubleValue() : DEFAULT_TEMPERATURE);

            ArrayNode messages = root.putArray("messages");
            if (session.getSystemPrompt() != null && !session.getSystemPrompt().isBlank()) {
                ObjectNode sys = messages.addObject();
                sys.put("role", "system");
                sys.put("content", session.getSystemPrompt());
            }
            // context window: include recent messages from session history
            int ctxSize = session.getContextSize() != null ? session.getContextSize() : 10;
            List<ChatMessage> history = chatMessageService.listRecentBySessionId(session.getId(), ctxSize);
            for (ChatMessage msg : history) {
                ObjectNode m = messages.addObject();
                m.put("role", msg.getRole());
                m.put("content", msg.getContent());
            }
            // append current user message (not yet saved)
            ObjectNode userMsg = messages.addObject();
            userMsg.put("role", "user");
            userMsg.put("content", userContent);

            String body = MAPPER.writeValueAsString(root);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + provider.getApiKey())
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new BusinessException("AI provider returned status " + response.statusCode());
            }
            JsonNode json = MAPPER.readTree(response.body());
            return json.path("choices").path(0).path("message").path("content").asText();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("AI chat error", e);
            throw new BusinessException("AI chat failed: " + e.getMessage());
        }
    }
}