package com.lzw.blueprint.workflow.service.impl;

import com.lzw.blueprint.common.exception.BusinessException;
import com.lzw.blueprint.workflow.entity.WfCopy;
import com.lzw.blueprint.workflow.mapper.WfCopyMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WfCopyServiceImplTest {

    private final WfCopyMapper copyMapper = mock(WfCopyMapper.class);
    private final WfCopyServiceImpl service = new WfCopyServiceImpl(copyMapper);

    @Test
    void copyInsertsOneRowPerUser() {
        when(copyMapper.insert(any())).thenReturn(1);

        int rows = service.copy("inst-1", List.of(2L, 3L));

        assertEquals(2, rows);
        ArgumentCaptor<WfCopy> captor = ArgumentCaptor.forClass(WfCopy.class);
        verify(copyMapper, times(2)).insert(captor.capture());
        List<WfCopy> copies = captor.getAllValues();
        assertEquals("inst-1", copies.get(0).getInstanceId());
        assertEquals("UNREAD", copies.get(0).getReadStatus());
        assertEquals(2L, copies.get(0).getUserId());
        assertEquals(3L, copies.get(1).getUserId());
    }

    @Test
    void copyRequiresInstanceAndUsers() {
        assertThrows(BusinessException.class, () -> service.copy(" ", List.of(1L)));
        assertThrows(BusinessException.class, () -> service.copy("inst-1", List.of()));
        assertThrows(BusinessException.class, () -> service.copy("inst-1", null));
    }

    @Test
    void listByUserDelegatesToMapper() {
        List<WfCopy> rows = List.of(new WfCopy());
        when(copyMapper.selectList(any())).thenReturn(rows);

        assertEquals(rows, service.listByUser(5L));
    }

    @Test
    void markReadSetsStatusAndTime() {
        WfCopy copy = new WfCopy();
        copy.setId(1L);
        copy.setReadStatus("UNREAD");
        when(copyMapper.selectById(1L)).thenReturn(copy);
        when(copyMapper.updateById(any())).thenReturn(1);

        assertEquals(1, service.markRead(1L));

        assertEquals("READ", copy.getReadStatus());
        assertNotNull(copy.getReadTime());
    }

    @Test
    void markReadUnknownThrows404() {
        when(copyMapper.selectById(99L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.markRead(99L));
        assertEquals(404, ex.getCode());
    }
}
