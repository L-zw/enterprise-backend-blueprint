package com.lzw.blueprint.workflow.service;

import com.lzw.blueprint.workflow.dto.InstanceDto;
import com.lzw.blueprint.workflow.dto.StartInstanceDto;

public interface WfInstanceService {

    InstanceDto start(StartInstanceDto request, Long initiator);

    InstanceDto detail(String instanceId);

    void cancel(String instanceId, String reason);
}
