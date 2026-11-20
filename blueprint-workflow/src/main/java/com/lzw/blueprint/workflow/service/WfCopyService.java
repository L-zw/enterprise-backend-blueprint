package com.lzw.blueprint.workflow.service;

import com.lzw.blueprint.workflow.dto.TaskDto;
import com.lzw.blueprint.workflow.entity.WfCopy;

import java.util.List;

public interface WfCopyService {

    int copy(String instanceId, List<Long> userIds);

    List<WfCopy> listByUser(Long userId);

    int markRead(Long id);
}
