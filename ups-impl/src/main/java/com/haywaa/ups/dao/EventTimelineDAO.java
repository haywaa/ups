package com.haywaa.ups.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.haywaa.ups.domain.entity.EventTimelineDO;

/**
 * @description
 * @author: haywaa
 * @create: 2019-12-02 18:01
 */
public interface EventTimelineDAO {

    Integer insert(@Param("pojo") EventTimelineDO pojo);

    Integer insertList(@Param("pojos") List<EventTimelineDO> pojos);

    Long selectMaxId();

    List<EventTimelineDO> selectNextPage(@Param("nextId") long nextId, @Param("pageSize") int pageSize);
}
