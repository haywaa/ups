package com.haywaa.ups.domain.entity;

import com.haywaa.ups.domain.constants.ValidStatus;

import lombok.Data;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-25 17:36
 */
@Data
public class RoleDO extends BaseDO<Integer> {

    /**
     * 角色编号
     */
    private String code;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 系统编号
     */
    private String systemCode;

    /**
     * 可为空
     */
    private String moduleCode;

    /**
     * admin 且 moduleCode不为空为业务模块管理员，moduleCode为空为系统管理员
     */
    private Integer type;

    /**
     * @see ValidStatus#toString()
     */
    private String status;

    /**
     * 描述
     */
    private String comment;
}
