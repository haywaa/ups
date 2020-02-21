package com.haywaa.ups.web.convert;

import com.haywaa.ups.domain.entity.ModuleDO;
import com.haywaa.ups.web.vo.ModuleVO;

/**
 * @description
 * @author: haywaa
 * @create: 2019-12-02 16:16
 */
public class ModuleConvert {

    public static ModuleVO convertDO2VO(ModuleDO moduleDO) {
        if (moduleDO == null) {
            return null;
        }
        ModuleVO moduleVO = new ModuleVO();
        moduleVO.setCode(moduleDO.getCode());
        moduleVO.setName(moduleDO.getName());
        moduleVO.setSystemCode(moduleDO.getSystemCode());
        moduleVO.setStatus(moduleDO.getStatus());
        moduleVO.setId(moduleDO.getId());
        moduleVO.setCreator(moduleDO.getCreator());
        moduleVO.setModifier(moduleDO.getModifier());
        moduleVO.setCreatedTime(moduleDO.getCreatedTime());
        moduleVO.setUpdatedTime(moduleDO.getUpdatedTime());
        return moduleVO;
    }
}
