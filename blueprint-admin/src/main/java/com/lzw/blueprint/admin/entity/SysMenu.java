package com.lzw.blueprint.admin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lzw.blueprint.common.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统菜单 / 权限实体
 * 对应表 sys_menu，permission 字段为权限标识（如 sys:user:list）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
public class SysMenu extends BaseEntity {

    @NotBlank(message = "菜单名称不能为空")
    private String name;
    private String permission;
    private String path;
    private Long parentId;
    private Integer type;
    private Integer sort;
    private String icon;
    private Integer status;
}