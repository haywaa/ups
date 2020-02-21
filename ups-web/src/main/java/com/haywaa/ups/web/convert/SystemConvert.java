package com.haywaa.ups.web.convert;

import com.haywaa.ups.domain.entity.SystemDO;
import com.haywaa.ups.web.vo.SystemVO;

/**
 * @description
 * @author: haywaa
 * @create: 2019-12-02 16:02
 */
public class SystemConvert {

    public static SystemVO convertDO2VO(SystemDO systemDO) {
        if (systemDO == null) {
            return null;
        }
        SystemVO systemVO = new SystemVO();
        systemVO.setCode(systemDO.getCode());
        systemVO.setName(systemDO.getName());
        systemVO.setStatus(systemDO.getStatus());
        systemVO.setId(systemDO.getId());
        systemVO.setCreator(systemDO.getCreator());
        systemVO.setModifier(systemDO.getModifier());
        systemVO.setCreatedTime(systemDO.getCreatedTime());
        systemVO.setUpdatedTime(systemDO.getUpdatedTime());
        return systemVO;
    }
}
