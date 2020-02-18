package com.haywaa.ups.service;

import java.util.List;

import com.haywaa.ups.domain.bo.OperatorInfo;
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

    List<ModuleDO> selectAll(String systemCode, Integer status);

    /**
     * null or invalid throw {@link BizException}
     */
    void checkCodeIsValid(String systemCode, String code);
}
