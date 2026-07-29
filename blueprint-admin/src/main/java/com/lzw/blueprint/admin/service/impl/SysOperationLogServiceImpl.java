package com.lzw.blueprint.admin.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzw.blueprint.admin.entity.SysOperationLog;
import com.lzw.blueprint.admin.mapper.SysOperationLogMapper;
import com.lzw.blueprint.admin.service.SysOperationLogService;
import com.lzw.blueprint.common.PageQuery;
import com.lzw.blueprint.common.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class SysOperationLogServiceImpl implements SysOperationLogService {

    @Autowired
    private SysOperationLogMapper sysOperationLogMapper;

    @Override
    @Async
    public void saveAsync(SysOperationLog log) {
        sysOperationLogMapper.insert(log);
    }

    @Override
    public PageResult<SysOperationLog> selectPage(PageQuery query) {
        Page<SysOperationLog> page = Page.of(query.getPage(), query.getSize());
        Page<SysOperationLog> result = sysOperationLogMapper.selectPage(page, null);

        PageResult<SysOperationLog> pr = new PageResult<>();
        pr.setRecords(result.getRecords());
        pr.setTotal(result.getTotal());
        pr.setPage((int) result.getCurrent());
        pr.setSize((int) result.getSize());
        pr.setPages(result.getPages());
        return pr;
    }
}