package com.lzw.blueprint.common;

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
    private LocalDateTime createTime;
    /** 更新时间 */
    private LocalDateTime updateTime;
    /** 创建人 */
    private String createBy;
    /** 更新人 */
    private String updateBy;
    /** 逻辑删除标记 */
    private Boolean deleted;
    /** 乐观锁版本号 */
    private Integer version;
}