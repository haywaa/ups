package com.haywaa.ups.dao;

import org.apache.ibatis.annotations.Param;
import java.util.List;
import com.haywaa.ups.domain.entity.UserDO;

public interface UserDAO {

    int addOrUpdate(@Param("pojo") UserDO pojo);

    int insertList(@Param("pojos") List< UserDO> pojo);

    List<UserDO> select(@Param("pojo") UserDO pojo);

    int update(@Param("pojo") UserDO pojo);

    UserDO selectById(@Param("id") Long id);

    Long selectUserIdByCode(@Param("channel") String channel, @Param("usercode") String usercode);

    Long selectUserIdByThirdId(@Param("channel") String channel, @Param("thirdId") String thirdId);
}
