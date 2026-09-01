package com.lzw.blueprint.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lzw.blueprint.ai.entity.PromptTemplate;
import com.lzw.blueprint.ai.mapper.PromptTemplateMapper;
import com.lzw.blueprint.ai.service.prompt.PromptRenderer;
import com.lzw.blueprint.ai.service.prompt.PromptTemplateService;
import com.lzw.blueprint.common.exception.BusinessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PromptTemplateServiceImpl implements PromptTemplateService {

    private final PromptTemplateMapper promptTemplateMapper;
    private final PromptRenderer promptRenderer;

    public PromptTemplateServiceImpl(PromptTemplateMapper promptTemplateMapper, PromptRenderer promptRenderer) {
        this.promptTemplateMapper = promptTemplateMapper;
        this.promptRenderer = promptRenderer;
    }

    @Override
    public List<PromptTemplate> list() {
        return promptTemplateMapper.selectList(new QueryWrapper<PromptTemplate>().orderByAsc("sort_order"));
    }

    @Override
    public PromptTemplate getById(Long id) {
        return promptTemplateMapper.selectById(id);
    }

    @Override
    public int create(PromptTemplate template) {
        if (template.getIsBuiltin() == null) template.setIsBuiltin(false);
        if (template.getSortOrder() == null) template.setSortOrder(0);
        if (template.getEnabled() == null) template.setEnabled(true);
        return promptTemplateMapper.insert(template);
    }

    @Override
    public int update(PromptTemplate template) {
        return promptTemplateMapper.updateById(template);
    }

    @Override
    public int delete(Long id) {
        PromptTemplate template = promptTemplateMapper.selectById(id);
        if (template != null && Boolean.TRUE.equals(template.getIsBuiltin())) {
            throw new BusinessException(400, "内置模板不可删除");
        }
        return promptTemplateMapper.deleteById(id);
    }

    @Override
    public String render(Long id, Map<String, String> variables) {
        PromptTemplate template = promptTemplateMapper.selectById(id);
        if (template == null) {
            throw new BusinessException(404, "模板不存在");
        }
        return promptRenderer.render(template.getContent(), variables);
    }
}
