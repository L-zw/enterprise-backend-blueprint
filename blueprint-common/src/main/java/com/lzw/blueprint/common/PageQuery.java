package com.lzw.blueprint.common;

import lombok.Data;

/**
 * 分页查询参数
 * Controller 接收分页请求时使用，默认第1页，每页10条
 */
@Data
public class PageQuery {

    /** 当前页码，默认1 */
    private Integer page = 1;
    /** 每页条数，默认10 */
    private Integer size = 10;
    /** 排序字段 */
    private String sort;
    /** 排序方向：asc / desc */
    private String order;
}