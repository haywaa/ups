package com.haywaa.ups.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.haywaa.ups.domain.entity.SystemDO;

public interface SystemDAO {

    int insert(@Param("pojo") SystemDO pojo);

    int insertList(@Param("pojos") List< SystemDO> pojo);

    int update(@Param("pojo") SystemDO pojo);

    List<SystemDO> select(@Param("pojo") SystemDO pojo);

    List<SystemDO> selectAll(Integer status);

    List<SystemDO> selectValidSystem();

    SystemDO selectByCode(@Param("code") String code);

}
