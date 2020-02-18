package com.haywaa.ups.rest.param;

import com.haywaa.ups.domain.constants.ResourceType;
import com.haywaa.ups.domain.constants.ValidStatus;
import com.haywaa.ups.domain.entity.ResourceDO;

import lombok.Data;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-27 15:27
 */
@Data
public class ResourceParam {

    private Integer id;

    /**
     * 编号
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
     */
    private Integer type;

    /**
     * 业务模块编号
     */
    private String moduleCode;

    /**
     * 系统编号
     */
    private String systemCode;

    /**
     * {@link ValidStatus#getCode()}
     * 状态 1 为启用， 0 为禁用
     */
    private Integer status;

    /**
     * 资源项
     */
    private String items;

    /**
     * 排序码
     */
    private Integer sortNum;

    public ResourceDO convertToDO() {
        ResourceDO resourceDO = new ResourceDO();
        resourceDO.setId(getId());
        resourceDO.setCode(getCode());
        resourceDO.setParentCode(getParentCode());
        resourceDO.setName(getName());
        resourceDO.setType(getType());
        resourceDO.setModuleCode(getModuleCode());
        resourceDO.setSystemCode(getSystemCode());
        resourceDO.setStatus(getStatus());
        resourceDO.setItems(getItems());
        resourceDO.setSortNum(getSortNum());
        return resourceDO;
    }
}
