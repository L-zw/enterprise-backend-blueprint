package com.lzw.blueprint.admin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lzw.blueprint.common.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统用户实体
 * 对应表 sys_user
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {

    /** 登录用户名 */
    @NotBlank(message = "用户名不能为空")
    private String username;
    /** 登录密码 */
    private String password;
    /** 真实姓名 */
    private String realName;
    /** 邮箱 */
    private String email;
    /** 手机号 */
    private String phone;
    /** 头像地址 */
    private String avatar;
    /** 状态：1=启用 0=禁用 */
    private Integer status;
    /** 部门ID */
    private Long deptId;
}
