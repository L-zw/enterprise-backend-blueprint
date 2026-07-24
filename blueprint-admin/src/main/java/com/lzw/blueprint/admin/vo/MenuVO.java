package com.lzw.blueprint.admin.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MenuVO {

    private Long id;
    private String name;
    private String permission;
    private String path;
    private String component;
    private Long parentId;
    private Integer type;
    private Integer sort;
    private String icon;
    private Integer status;
    private Boolean hidden;
    private String redirect;
    private List<MenuVO> children = new ArrayList<>();
}