package com.lzw.blueprint.ai.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lzw.blueprint.ai.dto.ChatResponseDto;
import com.lzw.blueprint.ai.dto.ModelInfoDto;
import com.lzw.blueprint.ai.entity.AiProvider;
import com.lzw.blueprint.ai.service.AiChatService;
import com.lzw.blueprint.ai.provider.ProviderRouter;
import com.lzw.blueprint.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 简易实现：使用标准 Java HttpClient 直接调用 OpenAI‑compatible 接口。
 * 只演示核心流程，未做完整错误处理与模型配置。
 */
@Service
@Slf4j
public class AiChatServiceImpl implements AiChatService {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final double DEFAULT_TEMPERATURE = 0.7;

    private final ProviderRouter providerRouter;

    public AiChatServiceImpl(ProviderRouter providerRouter) {
        this.providerRouter = providerRouter;
    }

    @Override
    public ChatResponseDto chat(String sessionId, String content) {
        AiProvider provider = providerRouter.resolveProvider(null);
        // 离线模式：没有可用供应商或缺少 key
        if (provider == null || provider.getApiKey() == null || provider.getApiKey().isBlank()) {
            ChatResponseDto fallback = new ChatResponseDto();
            fallback.setReply("[offline] No AI provider configured");
            return fallback;
        }
        try {
            String base = provider.getBaseUrl();
            if (base == null || base.isBlank()) {
                base = "https://api.openai.com";
            }
            String endpoint = base.endsWith("/") ? base + "v1/chat/completions" : base + "/v1/chat/completions";
            URI uri = URI.create(endpoint);

            // 构造请求体
            ObjectNode root = MAPPER.createObjectNode();
            String model = provider.getDefaultModel();
            if (model == null || model.isBlank()) {
                model = "gpt-3.5-turbo";
            }
            root.put("model", model);
            root.put("temperature", DEFAULT_TEMPERATURE);
            ArrayNode messages = root.putArray("messages");
            ObjectNode msg = messages.addObject();
            msg.put("role", "user");
            msg.put("content", content);
            String body = MAPPER.writeValueAsString(root);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + provider.getApiKey())
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new BusinessException("AI provider returned status " + response.statusCode());
            }
            JsonNode json = MAPPER.readTree(response.body());
            JsonNode choice = json.path("choices").path(0).path("message").path("content");
            String reply = choice.isMissingNode() ? "" : choice.asText();
            ChatResponseDto dto = new ChatResponseDto();
            dto.setReply(reply);
            dto.setProviderCode(provider.getProviderCode());
            return dto;
        } catch (Exception e) {
            log.error("AI chat error", e);
            throw new BusinessException("AI chat failed: " + e.getMessage());
        }
    }

    @Override
    public Flux<String> chatStream(String sessionId, String content) {
        // 简单包装：返回单一块的 Flux，实际流式可后续完善
        return Flux.just(chat(sessionId, content).getReply());
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
}
