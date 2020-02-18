package com.haywaa.ups.dao;

import org.apache.ibatis.annotations.Param;
import java.util.List;
import com.haywaa.ups.domain.entity.UserRoleDO;

public interface UserRoleDAO {

    int insert(@Param("pojo") UserRoleDO pojo);

    int insertList(@Param("pojos") List< UserRoleDO> pojo);

    List<UserRoleDO> select(@Param("pojo") UserRoleDO pojo);

    int update(@Param("pojo") UserRoleDO pojo);

    Integer removeList(Long userId, List<Integer> roleIds);
}
