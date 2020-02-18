package com.haywaa.ups.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.haywaa.ups.domain.entity.ModuleDO;

public interface ModuleDAO {

    int insert(@Param("pojo") ModuleDO pojo);

    int insertList(@Param("pojos") List< ModuleDO> pojo);

    List<ModuleDO> select(@Param("pojo") ModuleDO pojo);

    int update(@Param("pojo") ModuleDO pojo);

    ModuleDO selectByCode(@Param("systemCode") String systemCode, @Param("code") String code);

    ModuleDO selectById(@Param("id") Integer id);

    List<ModuleDO> selectAll(String systemCode, Integer status);
}
