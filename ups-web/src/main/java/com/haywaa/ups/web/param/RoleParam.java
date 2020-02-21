package com.haywaa.ups.web.param;

import com.haywaa.ups.domain.constants.RoleType;
import com.haywaa.ups.domain.constants.ValidStatus;
import com.haywaa.ups.domain.entity.RoleDO;

import lombok.Data;

/**
 * @description
 * @author: haywaa
 * @create: 2019-12-02 15:41
 */
@Data
public class RoleParam {

    private Integer id;

    /**
     * 角色编号
     */
    private String code;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 系统编号
     */
    private String systemCode;

    /**
     * 可为空
     */
    private String moduleCode;

    /**
     * admin 且 moduleCode不为空为业务模块管理员，moduleCode为空为系统管理员
     * @see RoleType#toString()
     */
    private String type;

    /**
     * @see ValidStatus#toString()
     */
    private String status;

    /**
     * 描述
     */
    private String comment;

    public RoleDO convertToDO() {
        RoleDO roleDO = new RoleDO();
        roleDO.setCode(this.getCode());
        roleDO.setName(this.getName());
        roleDO.setSystemCode(this.getSystemCode());
        roleDO.setModuleCode(this.getModuleCode());
        roleDO.setType(this.getType());
        roleDO.setStatus(this.getStatus());
        roleDO.setComment(this.getComment());
        roleDO.setId(this.getId());
        return roleDO;
    }
}
