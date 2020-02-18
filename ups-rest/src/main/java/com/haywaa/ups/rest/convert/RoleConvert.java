package com.haywaa.ups.rest.convert;

import com.haywaa.ups.domain.entity.RoleDO;
import com.haywaa.ups.rest.vo.RoleVO;

/**
 * @description
 * @author: haywaa
 * @create: 2019-12-02 17:31
 */
public class RoleConvert {

    public static RoleVO convertDO2VO(RoleDO roleDO) {
        if (roleDO == null) {
            return null;
        }
        RoleVO roleVO = new RoleVO();
        roleVO.setCode(roleDO.getCode());
        roleVO.setName(roleDO.getName());
        roleVO.setSystemCode(roleDO.getSystemCode());
        roleVO.setModuleCode(roleDO.getModuleCode());
        roleVO.setType(roleDO.getType());
        roleVO.setStatus(roleDO.getStatus());
        roleVO.setComment(roleDO.getComment());
        roleVO.setId(roleDO.getId());
        roleVO.setCreator(roleDO.getCreator());
        roleVO.setModifier(roleDO.getModifier());
        roleVO.setCreatedTime(roleDO.getCreatedTime());
        roleVO.setUpdatedTime(roleDO.getUpdatedTime());
        return roleVO;
    }
}
