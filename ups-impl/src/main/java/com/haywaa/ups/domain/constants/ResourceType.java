package com.haywaa.ups.domain.constants;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-25 19:25
 */
public enum ResourceType {

    /**
     * 分组
     */
    GROUP(1, "分组"),

    /**
     * 操作
     */
    ACTION(2, "操作");

    private final int code;

    private final String zhName;

    ResourceType(int code, String zhName) {
        this.code = code;
        this.zhName = zhName;
    }

    public int getCode() {
        return code;
    }

    public String getZhName() {
        return zhName;
    }
}
