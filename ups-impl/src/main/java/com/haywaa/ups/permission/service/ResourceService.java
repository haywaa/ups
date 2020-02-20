package com.haywaa.ups.permission.service;

import java.util.List;

import com.haywaa.ups.permission.bo.OperatorInfo;
import com.haywaa.ups.domain.entity.ResourceDO;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-25 19:08
 */
public interface ResourceService {

    Integer insert(ResourceDO resourceDO, OperatorInfo operator);

    void update(ResourceDO resourceDO, OperatorInfo operator);

    List<ResourceDO> selectAll(String systemCode, String status);
}
