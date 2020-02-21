package com.haywaa.ups.utils;

import com.haywaa.ups.domain.constants.Constants;
import com.haywaa.ups.domain.constants.RoleType;
import com.haywaa.ups.domain.entity.RoleDO;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-29 17:53
 */
public class AuthUtil {

    public static boolean isSuperAdmin(RoleDO role) {
        return RoleType.ROOT.toString().equals(role.getType()) && Constants.SYSTEM_UPS.equals(role.getSystemCode());
    }

    public static boolean isSystemAdmin(RoleDO role, String systemCode) {
        if (role == null || systemCode == null) {
            return false;
        }
        return RoleType.ADMIN.toString().equals(role.getType()) && role.getSystemCode().equals(systemCode);
    }
}
