package com.lzw.blueprint.admin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lzw.blueprint.common.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统角色实体
 * 对应表 sys_role
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class SysRole extends BaseEntity {

    /** 角色名称 */
    @NotBlank(message = "角色名称不能为空")
    private String name;
    /** 角色编码 */
    @NotBlank(message = "角色编码不能为空")
    private String code;
    /** 状态：1=启用 0=禁用 */
    private Integer status;
}
