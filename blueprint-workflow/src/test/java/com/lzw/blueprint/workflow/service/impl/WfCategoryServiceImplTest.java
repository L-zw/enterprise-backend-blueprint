package com.lzw.blueprint.workflow.service.impl;

import com.lzw.blueprint.common.exception.BusinessException;
import com.lzw.blueprint.workflow.entity.WfCategory;
import com.lzw.blueprint.workflow.mapper.WfCategoryMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WfCategoryServiceImplTest {

    private final WfCategoryMapper mapper = mock(WfCategoryMapper.class);
    private final WfCategoryServiceImpl service = new WfCategoryServiceImpl(mapper);

    private WfCategory newCategory() {
        WfCategory category = new WfCategory();
        category.setCode("leave");
        category.setName("请假审批");
        return category;
    }

    @Test
    void createAppliesDefaults() {
        when(mapper.selectCount(any())).thenReturn(0L);
        when(mapper.insert(any())).thenReturn(1);

        WfCategory category = newCategory();
        assertEquals(1, service.create(category));

        assertEquals(0, category.getSortOrder());
        assertTrue(category.getEnabled());
    }

    @Test
    void createKeepsExplicitValues() {
        when(mapper.selectCount(any())).thenReturn(0L);
        when(mapper.insert(any())).thenReturn(1);

        WfCategory category = newCategory();
        category.setSortOrder(9);
        category.setEnabled(false);
        service.create(category);

        assertEquals(9, category.getSortOrder());
        assertFalse(category.getEnabled());
    }

    @Test
    void createRejectsDuplicateCode() {
        when(mapper.selectCount(any())).thenReturn(1L);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.create(newCategory()));
        assertTrue(ex.getMessage().contains("已存在"));
        assertEquals(400, ex.getCode());
        verify(mapper, never()).insert(any());
    }

    @Test
    void listReturnsCategories() {
        List<WfCategory> categories = List.of(newCategory());
        when(mapper.selectList(any())).thenReturn(categories);

        assertEquals(categories, service.list());
    }

    @Test
    void getByIdDelegatesToMapper() {
        when(mapper.selectById(1L)).thenReturn(newCategory());

        assertEquals("leave", service.getById(1L).getCode());
    }

    @Test
    void updateAndDeleteDelegateToMapper() {
        WfCategory category = newCategory();
        category.setId(1L);
        when(mapper.updateById(any())).thenReturn(1);
        when(mapper.deleteById(1L)).thenReturn(1);

        assertEquals(1, service.update(category));
        assertEquals(1, service.delete(1L));
    }
}
