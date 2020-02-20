package com.haywaa.ups.permission.bo;

import java.util.List;

import com.haywaa.ups.domain.entity.RoleDO;
import com.haywaa.ups.user.bo.UserBO;

import lombok.Data;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-25 18:06
 */
@Data
public class UserPermissionBO {

    private UserBO userInfo;

    private List<RoleDO> roleList;

    private List<ResourceBO> resouceList;
}
