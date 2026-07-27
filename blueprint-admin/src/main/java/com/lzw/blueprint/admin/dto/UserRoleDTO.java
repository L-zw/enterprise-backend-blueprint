package com.lzw.blueprint.admin.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserRoleDTO {

    private List<Long> roleIds;
}