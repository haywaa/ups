package com.haywaa.ups.domain.constants;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-27 10:04
 */
public enum ValidStatus {

    /**
     *
     */
    INVALID("已禁用"),

    /**
     * 菜单
     */
    VALID("已启用");

    private final String zhName;

    ValidStatus(String zhName) {
        this.zhName = zhName;
    }

    public String getZhName() {
        return zhName;
    }

    public static ValidStatus codeOf(String value) {
        if (value == null) {
            return null;
        }

        for (ValidStatus item : ValidStatus.values()) {
            if (item.toString().equals(value)) {
                return item;
            }
        }

         return null;
    }
}
