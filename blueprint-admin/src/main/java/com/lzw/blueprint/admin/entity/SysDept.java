package com.lzw.blueprint.admin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lzw.blueprint.common.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统部门实体
 * 对应表 sys_dept
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dept")
public class SysDept extends BaseEntity {

    /** 部门名称 */
    @NotBlank(message = "部门名称不能为空")
    private String name;
    /** 父级部门ID，顶级为0 */
    private Long parentId;
    /** 排序 */
    private Integer sort;
    /** 状态：1=启用 0=禁用 */
    private Integer status;
}
