package com.lzw.blueprint.workflow.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lzw.blueprint.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wf_copy")
public class WfCopy extends BaseEntity {

    private String instanceId;
    private Long userId;
    private String readStatus;
    private LocalDateTime readTime;
}
