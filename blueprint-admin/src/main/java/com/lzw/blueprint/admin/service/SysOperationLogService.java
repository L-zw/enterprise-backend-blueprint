package com.lzw.blueprint.admin.service;

import com.lzw.blueprint.admin.entity.SysOperationLog;
import com.lzw.blueprint.common.PageQuery;
import com.lzw.blueprint.common.PageResult;

public interface SysOperationLogService {

    void saveAsync(SysOperationLog log);

    PageResult<SysOperationLog> selectPage(PageQuery query);
}