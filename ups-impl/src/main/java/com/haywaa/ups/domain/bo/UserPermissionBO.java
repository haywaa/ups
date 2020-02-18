package com.haywaa.ups.domain.bo;

import java.util.List;

import com.haywaa.ups.domain.entity.ResourceDO;
import com.haywaa.ups.domain.entity.RoleDO;
import com.haywaa.ups.domain.entity.UserDO;

import lombok.Data;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-25 18:06
 */
@Data
public class UserPermissionBO {

    private UserDO userInfo;

    private List<RoleDO> roleList;

    private List<ResourceDO> resouceList;
}
