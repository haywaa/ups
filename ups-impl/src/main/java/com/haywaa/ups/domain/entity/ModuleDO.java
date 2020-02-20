package com.haywaa.ups.domain.entity;

import com.haywaa.ups.domain.constants.ValidStatus;

import lombok.Data;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-25 17:37
 */
@Data
public class ModuleDO extends BaseDO<Integer> {

    /**
     * 编号
     */
    private String code;

    /**
     * 名称
     */
    private String name;

    /**
     * 系统编号
     */
    private String systemCode;

    /**
     * 状态 {@link ValidStatus#toString()}
     */
    private String status;
}