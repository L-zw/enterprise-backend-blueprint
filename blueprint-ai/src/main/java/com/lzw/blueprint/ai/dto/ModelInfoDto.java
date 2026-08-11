package com.lzw.blueprint.ai.dto;

import lombok.Data;

/**
 * 模型信息 DTO，仅返回模型标识和所属供应商。
 */
@Data
public class ModelInfoDto {
    private String modelId;
    private String providerCode;
}
