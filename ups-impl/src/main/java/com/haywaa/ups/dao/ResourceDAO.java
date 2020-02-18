package com.haywaa.ups.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.haywaa.ups.domain.entity.ModuleDO;
import com.haywaa.ups.domain.entity.ResourceDO;

public interface ResourceDAO {

    int insert(@Param("pojo") ResourceDO pojo);

    int insertList(@Param("pojos") List< ResourceDO> pojo);

    List<ResourceDO> select(@Param("pojo") ResourceDO pojo);

    int update(@Param("pojo") ResourceDO pojo);

    ResourceDO selectByCode(@Param("systemCode") String systemCode, @Param("code") String code);

    ResourceDO selectById(@Param("id") Integer id);

    List<ResourceDO> selectAll(String systemCode, Integer status);

    List<ResourceDO> selectBySystemCode(@Param("systemCode") String systemCode, @Param("status") Integer status);
}
