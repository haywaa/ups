package com.haywaa.ups.permission.bo;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;
import com.haywaa.ups.domain.constants.ResourceType;
import com.haywaa.ups.domain.constants.ValidStatus;

import lombok.Data;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-25 18:13
 */
@Data
public class ResourceBO {

    /**
     * 编号
     * 不允许修改
     */
    private String code;

    /**
     * 资源类型 {@link ResourceType#getCode()}
     * 不允许修改
     */
    private Integer type;

    /**
     * 资源项
     */
    private String items;

    /**
     * 拥有该资源权限的角色ID
     */
    private List<Integer> relatedRoleIds;

    /**
     * @see ValidStatus#toString()
     */
    private String status;

    /**
     * 编号
     * 不允许修改
     */
    @JSONField(serialize = false)
    private String parentCode;

    /**
     * 排序码
     */
    @JSONField(serialize = false)
    private Integer sortNum;
}
