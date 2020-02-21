package com.haywaa.ups.web.web;

import com.haywaa.ups.domain.constants.ErrorCode;
import com.haywaa.ups.domain.exception.BizException;
import com.haywaa.ups.permission.bo.OperatorInfo;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-27 09:47
 */
public class OperateContext {

    private static final ThreadLocal<OperatorInfo> CONTEXT_HOLDER = ThreadLocal.withInitial(
            () -> null);

    public static void set(OperatorInfo operatorInfo) {
        CONTEXT_HOLDER.set(operatorInfo);
    }

    public static OperatorInfo get() {
        return CONTEXT_HOLDER.get();
    }

    public static OperatorInfo getNotNull() {
        OperatorInfo operatorInfo = CONTEXT_HOLDER.get();
        if (operatorInfo == null) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "获取操作人信息失败");
        }
        return operatorInfo;
    }

    public static void clear() {
        CONTEXT_HOLDER.remove();
    }
}
