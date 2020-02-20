package com.haywaa.ups.permission.service;

import java.util.List;

import com.haywaa.ups.permission.bo.OperatorInfo;
import com.haywaa.ups.domain.entity.ModuleDO;
import com.haywaa.ups.domain.exception.BizException;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-27 10:27
 */
public interface ModuleService {

    Integer insert(ModuleDO moduleDO, OperatorInfo operator);

    void update(ModuleDO moduleDO, OperatorInfo operator);

    List<ModuleDO> selectAll(String systemCode, String status);

    /**
     * null or invalid throw {@link BizException}
     */
    void checkCodeIsValid(String systemCode, String code);
}
