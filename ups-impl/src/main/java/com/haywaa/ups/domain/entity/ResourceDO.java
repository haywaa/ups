package com.haywaa.ups.domain.entity;

import com.haywaa.ups.domain.constants.ResourceType;
import com.haywaa.ups.domain.constants.ValidStatus;

import lombok.Data;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-25 17:36
 */
@Data
public class ResourceDO extends BaseDO<Integer> {

    /**
     * 编号
     * 不允许修改
     */
    private String code;

    /**
     * 父资源编号
     */
    private String parentCode;

    /**
     * 名称
     */
    private String name;

    /**
     * 资源类型 {@link ResourceType#getCode()}
     * 不允许修改
     */
    private Integer type;

    /**
     * 业务模块编号
     * 不允许修改
     */
    private String moduleCode;

    /**
     * 系统编号
     * 不允许修改
     */
    private String systemCode;

    /**
     * {@link ValidStatus#toString()}
     * 状态 1 为启用中， 0 为已禁用
     */
    private String status;

    /**
     * 资源项
     */
    private String items;

    /**
     * 扩展信息
     */
    private String feature;

    /**
     * 排序码
     */
    private Integer sortNum;
}
