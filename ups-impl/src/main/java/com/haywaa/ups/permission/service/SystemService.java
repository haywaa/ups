package com.haywaa.ups.permission.service;

import java.util.List;

import com.haywaa.ups.permission.bo.OperatorInfo;
import com.haywaa.ups.domain.entity.SystemDO;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-26 16:34
 */
public interface SystemService {

    Integer insert(SystemDO systemDO, OperatorInfo operator);

    void update(SystemDO systemDO, OperatorInfo operator);

    List<SystemDO> selectAll(String status);

    /**
     * null or invalid throw {@link com.haywaa.ups.domain.exception.BizException}
     */
    void checkCodeIsValid(String code);
}
