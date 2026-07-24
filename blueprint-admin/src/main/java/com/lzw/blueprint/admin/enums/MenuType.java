package com.lzw.blueprint.admin.enums;

import lombok.Getter;

@Getter
public enum MenuType {

    DIR(1, "目录"),
    MENU(2, "菜单"),
    BTN(3, "按钮");

    private final Integer value;
    private final String label;

    MenuType(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}