package com.haywaa.ups.rpc.enums;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-25 19:25
 */
public enum ResourceType {

    /**
     * 分组
     */
    GROUP("分组"),

    /**
     * 操作
     */
    ACTION("操作");

    private final String zhName;

    ResourceType(String zhName) {
        this.zhName = zhName;
    }

    public String getZhName() {
        return zhName;
    }
}
