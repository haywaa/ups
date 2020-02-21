package com.haywaa.ups.web.web;

import lombok.Data;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-26 11:18
 */
@Data
public class HttpResult<T> {
    private int code;
    private String message;
    private T data;

    public static HttpResult Success() {
        return Success(null);
    }

    public static <T> HttpResult Success(T data) {
        HttpResult result = new HttpResult();
        result.setCode(0);
        result.setData(data);
        return result;
    }

    public static <T> HttpResult Failure(int code, String message) {
        HttpResult result = new HttpResult();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}
