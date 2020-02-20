package com.haywaa.ups.permission.convert;

import com.haywaa.ups.domain.entity.ResourceDO;
import com.haywaa.ups.permission.bo.ResourceBO;

/**
 * @description
 * @author: qingye
 * @create: 2020-02-20 23:26
 */
public class ResourceConvert {
    public static ResourceBO convertDoToBO(ResourceDO resourceDO) {
        if (resourceDO == null) {
            return null;
        }
        ResourceBO resourceBO = new ResourceBO();
        resourceBO.setCode(resourceDO.getCode());
        resourceBO.setParentCode(resourceDO.getParentCode());
        //resourceBO.setName(resourceDO.getName());
        resourceBO.setType(resourceDO.getType());
        //resourceBO.setModuleCode(resourceDO.getModuleCode());
        //resourceBO.setSystemCode(resourceDO.getSystemCode());
        //resourceBO.setStatus(resourceDO.getStatus());
        resourceBO.setItems(resourceDO.getItems());
        //resourceBO.setFeature(resourceDO.getFeature());
        resourceBO.setSortNum(resourceDO.getSortNum());
        //resourceBO.setId(resourceDO.getId());
        //resourceBO.setCreator(resourceDO.getCreator());
        //resourceBO.setModifier(resourceDO.getModifier());
        //resourceBO.setCreatedTime(resourceDO.getCreatedTime());
        //resourceBO.setUpdatedTime(resourceDO.getUpdatedTime());
        return resourceBO;
    }
}
