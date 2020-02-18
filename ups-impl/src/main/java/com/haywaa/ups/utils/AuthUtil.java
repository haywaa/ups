package com.haywaa.ups.utils;

import java.util.List;

import org.springframework.util.CollectionUtils;

import com.haywaa.ups.domain.constants.RoleType;
import com.haywaa.ups.domain.entity.RoleDO;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-29 17:53
 */
public class AuthUtil {

    public static boolean isSuperAdmin(RoleDO role) {
        return RoleType.SUPER_ADMIN.getCode() == role.getType();
    }

    public static boolean isSystemAdmin(RoleDO role, String systemCode) {
        if (role == null || systemCode == null) {
            return false;
        }
        return RoleType.ADMIN.getCode() == role.getType() && role.getSystemCode().equals(systemCode);
    }

    public static boolean isSuperAdmin(List<RoleDO> roleList) {
        if (CollectionUtils.isEmpty(roleList)) {
            return false;
        }
        for (RoleDO role : roleList) {
            if (isSuperAdmin(role)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSystemAdmin(List<RoleDO> roleList, String systemCode) {
        if (CollectionUtils.isEmpty(roleList) || systemCode == null) {
            return false;
        }
        for (RoleDO role : roleList) {
            if (isSystemAdmin(role, systemCode)) {
                return true;
            }
        }
        return false;
    }

}
