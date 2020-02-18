package com.haywaa.ups.dao;

import org.apache.ibatis.annotations.Param;
import java.util.List;
import com.haywaa.ups.domain.entity.RoleResourceDO;

public interface RoleResourceDAO {

    int insert(@Param("pojo") RoleResourceDO pojo);

    int insertList(@Param("pojos") List< RoleResourceDO> pojo);

    List<RoleResourceDO> select(@Param("pojo") RoleResourceDO pojo);

    int update(@Param("pojo") RoleResourceDO pojo);

    List<Integer> selectResourceIds(@Param("roleId") Integer roleId);

    List<RoleResourceDO> selectByRoleIds(@Param("roleIds") List<Integer> roleIds);
}
