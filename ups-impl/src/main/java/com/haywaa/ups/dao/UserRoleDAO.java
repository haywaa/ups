package com.haywaa.ups.dao;

import org.apache.ibatis.annotations.Param;
import java.util.List;
import com.haywaa.ups.domain.entity.UserRoleDO;

public interface UserRoleDAO {

    int insert(@Param("pojo") UserRoleDO pojo);

    int insertList(@Param("pojos") List< UserRoleDO> pojo);

    List<UserRoleDO> select(@Param("pojo") UserRoleDO pojo);

    List<UserRoleDO> selectByUserId(@Param("userId") Long userId, @Param("channel") String channel, @Param("systemCode") String systemCode);

    int update(@Param("pojo") UserRoleDO pojo);

    Integer removeList(@Param("userId") Long userId, @Param("channel") String channel, @Param("roleItems") List<String> roleItems);
}
