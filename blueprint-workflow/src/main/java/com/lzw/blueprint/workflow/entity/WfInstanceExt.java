package com.lzw.blueprint.workflow.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lzw.blueprint.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wf_instance_ext")
public class WfInstanceExt extends BaseEntity {

    private String instanceId;
    private String bpmnKey;
    private Long initiator;
    private String title;
    private String formData;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
