package com.lzw.blueprint.common;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实体基类
 * 所有数据库实体类继承此类，统一审计字段
 */
@Data
public class BaseEntity {

    /** 主键ID */
    private Long id;
    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    /** 创建人 */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;
    /** 更新人 */
    @TableField(fill = FieldFill.UPDATE)
    private String updateBy;
    /** 逻辑删除标记 */
    @TableLogic
    private Boolean deleted;
    /** 乐观锁版本号 */
    @Version
    private Integer version;
}