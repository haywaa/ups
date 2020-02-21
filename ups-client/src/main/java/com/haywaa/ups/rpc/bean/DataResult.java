package com.haywaa.ups.rpc.bean;

import lombok.Data;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-26 11:18
 */
@Data
public class DataResult<T> {
    private int code;
    private String message;
    private T data;

    public static DataResult Success() {
        return Success(null);
    }

    public static <T> DataResult Success(T data) {
        DataResult result = new DataResult();
        result.setCode(0);
        result.setData(data);
        return result;
    }

    public static <T> DataResult Failure(int code, String message) {
        DataResult result = new DataResult();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}
