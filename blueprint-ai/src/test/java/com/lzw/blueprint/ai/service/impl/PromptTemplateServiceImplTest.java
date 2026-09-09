package com.lzw.blueprint.ai.service.impl;

import com.lzw.blueprint.ai.entity.PromptTemplate;
import com.lzw.blueprint.ai.mapper.PromptTemplateMapper;
import com.lzw.blueprint.ai.service.prompt.PromptRenderer;
import com.lzw.blueprint.common.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PromptTemplateServiceImplTest {

    @Mock
    private PromptTemplateMapper promptTemplateMapper;
    @Mock
    private PromptRenderer promptRenderer;

    private PromptTemplateServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PromptTemplateServiceImpl(promptTemplateMapper, promptRenderer);
    }

    private PromptTemplate template(Long id, boolean builtin) {
        PromptTemplate t = new PromptTemplate();
        t.setId(id);
        t.setName("t");
        t.setIsBuiltin(builtin);
        return t;
    }

    @Test
    void listOrdersBySortOrder() {
        when(promptTemplateMapper.selectList(any())).thenReturn(List.of(template(1L, false)));
        assertThat(service.list()).hasSize(1);
    }

    @Test
    void createFillsDefaults() {
        when(promptTemplateMapper.insert(any())).thenReturn(1);
        PromptTemplate t = new PromptTemplate();
        service.create(t);
        assertThat(t.getIsBuiltin()).isFalse();
        assertThat(t.getSortOrder()).isZero();
        assertThat(t.getEnabled()).isTrue();
    }

    @Test
    void updateCallsMapper() {
        when(promptTemplateMapper.updateById(any())).thenReturn(1);
        assertThat(service.update(template(1L, false))).isEqualTo(1);
    }

    @Test
    void deleteBuiltinThrows400() {
        when(promptTemplateMapper.selectById(1L)).thenReturn(template(1L, true));
        assertThatThrownBy(() -> service.delete(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("内置模板不可删除");
        verify(promptTemplateMapper, never()).deleteById(1L);
    }

    @Test
    void deleteNonBuiltinCallsMapper() {
        when(promptTemplateMapper.selectById(1L)).thenReturn(template(1L, false));
        when(promptTemplateMapper.deleteById(1L)).thenReturn(1);
        assertThat(service.delete(1L)).isEqualTo(1);
    }

    @Test
    void renderMissingTemplateThrows404() {
        when(promptTemplateMapper.selectById(1L)).thenReturn(null);
        assertThatThrownBy(() -> service.render(1L, Map.of()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("模板不存在");
    }

    @Test
    void renderDelegatesToRenderer() {
        PromptTemplate t = template(1L, false);
        t.setContent("请解释 ${code}");
        when(promptTemplateMapper.selectById(1L)).thenReturn(t);
        when(promptRenderer.render("请解释 ${code}", Map.of("code", "x"))).thenReturn("请解释 x");

        assertThat(service.render(1L, Map.of("code", "x"))).isEqualTo("请解释 x");
    }
}
