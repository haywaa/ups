package com.haywaa.ups.user.plugin;

import org.springframework.beans.factory.InitializingBean;

import com.haywaa.ups.domain.constants.ErrorCode;
import com.haywaa.ups.domain.entity.UserDO;
import com.haywaa.ups.user.UserPluginManager;

/**
 * @description
 * @author: qingye
 * @create: 2020-02-19 12:31
 */
public abstract class AbstractUserPlugin implements UserPlugin, InitializingBean {

    protected abstract String getChannel();

    @Override
    public void afterPropertiesSet() throws Exception {
        UserPluginManager.addPlugin(getChannel(), this);
    }

    @Override
    public Long addOrUpdate(UserDO userDO) {
        throw ErrorCode.SERVICE_ERROR.toBizException("当前渠道不支持通过该接口新增用户");
    }
}
