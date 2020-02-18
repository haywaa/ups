package com.haywaa.ups.dao;

import org.apache.ibatis.annotations.Param;
import java.util.List;
import com.haywaa.ups.domain.entity.SettingDO;

public interface SettingDAO {

    int insert(@Param("pojo") SettingDO pojo);

    int insertList(@Param("pojos") List< SettingDO> pojo);

    List<SettingDO> select(@Param("pojo") SettingDO pojo);

    int update(@Param("pojo") SettingDO pojo);

}
