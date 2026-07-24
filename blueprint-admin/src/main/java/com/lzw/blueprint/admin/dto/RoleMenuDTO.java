package com.lzw.blueprint.admin.dto;

import lombok.Data;

import java.util.List;

@Data
public class RoleMenuDTO {

    private List<Long> menuIds;
    private List<Long> halfIds;
}