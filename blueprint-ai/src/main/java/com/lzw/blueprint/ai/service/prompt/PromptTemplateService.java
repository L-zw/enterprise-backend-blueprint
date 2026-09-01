package com.lzw.blueprint.ai.service.prompt;

import com.lzw.blueprint.ai.entity.PromptTemplate;

import java.util.List;
import java.util.Map;

public interface PromptTemplateService {

    List<PromptTemplate> list();

    PromptTemplate getById(Long id);

    int create(PromptTemplate template);

    int update(PromptTemplate template);

    int delete(Long id);

    String render(Long id, Map<String, String> variables);
}
