package com.lzw.blueprint.ai.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lzw.blueprint.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("prompt_template")
public class PromptTemplate extends BaseEntity {

    private String name;
    private String category;
    private String content;
    private String variables;
    private String icon;
    private Boolean isBuiltin;
    private Integer sortOrder;
    private Boolean enabled;
}
