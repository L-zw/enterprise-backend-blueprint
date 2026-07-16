package com.lzw.blueprint.common;

import lombok.Data;

import java.util.List;

/**
 * 分页响应体
 * 统一返回分页数据，前端直接使用
 */
@Data
public class PageResult<T> {

    /** 当前页数据 */
    private List<T> records;
    /** 总记录数 */
    private Long total;
    /** 当前页码 */
    private Integer page;
    /** 每页条数 */
    private Integer size;
    /** 总页数 */
    private Long pages;
}