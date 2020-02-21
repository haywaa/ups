package com.haywaa.ups.domain.constants;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-26 11:10
 */
public enum RoleType {

    /**
     * 超级管理员, 所有权限，包括权限系统配置管理等
     */
    ROOT("超级管理员"),

    /**
     * 管理员，如系统管理员，业务模块管理员
     * 系统管理员可对系统下所有内容进行管理如资源管理、角色管理
     */
    ADMIN("管理员"),

    /**
     * 拥有用户角色分配权限
     */
    AUTH_ADMIN("权限管理员");

    private final String zhName;

    RoleType(String zhName) {
        this.zhName = zhName;
    }

    public String getZhName() {
        return zhName;
    }
}
