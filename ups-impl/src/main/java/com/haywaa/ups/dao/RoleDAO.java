package com.haywaa.ups.dao;

import org.apache.ibatis.annotations.Param;
import java.util.List;
import com.haywaa.ups.domain.entity.RoleDO;
import com.haywaa.ups.domain.query.RoleQuery;

public interface RoleDAO {

    int insert(@Param("pojo") RoleDO pojo);

    int insertList(@Param("pojos") List< RoleDO> pojo);

    List<RoleDO> select(@Param("pojo") RoleDO pojo);

    int update(@Param("pojo") RoleDO pojo);

    RoleDO selectByCode(@Param("systemCode") String systemCode, @Param("code") String code);

    RoleDO selectById(@Param("id") Integer id);

    List<RoleDO> selectByUserId(@Param("userId") Long userId, @Param("status") String status);

    List<RoleDO> selectBySystemCode(@Param("systemCode") String systemCode, @Param("status") String status);

    List<RoleDO> selectAll(@Param("query") RoleQuery query);
}
