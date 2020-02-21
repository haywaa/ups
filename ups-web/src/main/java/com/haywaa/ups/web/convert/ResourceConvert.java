package com.haywaa.ups.web.convert;

import com.haywaa.ups.domain.entity.ResourceDO;
import com.haywaa.ups.web.vo.ResourceVO;

/**
 * @description
 * @author: haywaa
 * @create: 2019-12-02 16:45
 */
public class ResourceConvert {

    public static ResourceVO convertDO2VO(ResourceDO resourceDO) {
        if (resourceDO == null) {
            return null;
        }
        ResourceVO resourceVO = new ResourceVO();
        resourceVO.setCode(resourceDO.getCode());
        resourceVO.setParentCode(resourceDO.getParentCode());
        resourceVO.setName(resourceDO.getName());
        resourceVO.setType(resourceDO.getType());
        resourceVO.setModuleCode(resourceDO.getModuleCode());
        resourceVO.setSystemCode(resourceDO.getSystemCode());
        resourceVO.setStatus(resourceDO.getStatus());
        resourceVO.setItems(resourceDO.getItems());
        //resourceVO.setFeature(resourceDO.getFeature());
        resourceVO.setSortNum(resourceDO.getSortNum());
        resourceVO.setId(resourceDO.getId());
        resourceVO.setCreator(resourceDO.getCreator());
        resourceVO.setModifier(resourceDO.getModifier());
        resourceVO.setCreatedTime(resourceDO.getCreatedTime());
        resourceVO.setUpdatedTime(resourceDO.getUpdatedTime());
        return resourceVO;
    }
}
