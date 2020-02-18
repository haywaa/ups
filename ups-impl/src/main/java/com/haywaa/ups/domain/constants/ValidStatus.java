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
    INVALID(0, "已禁用"),

    /**
     * 菜单
     */
    VALID(1, "已启用");

    private final int code;

    private final String zhName;

    ValidStatus(int code, String zhName) {
        this.code = code;
        this.zhName = zhName;
    }

    public int getCode() {
        return code;
    }

    public String getZhName() {
        return zhName;
    }

    public static ValidStatus codeOf(Integer code) {
        if (code == null) {
            return null;
        }

        for (ValidStatus value : ValidStatus.values()) {
            if (value.getCode() == code) {
                return value;
            }
        }

         return null;
    }
}
