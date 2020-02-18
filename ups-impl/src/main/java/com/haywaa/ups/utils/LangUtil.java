package com.haywaa.ups.utils;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-28 16:15
 */
public class LangUtil {

    public static Long parseLong(Object value, Long defaultValue) {
        if (value == null) {
            return null;
        }

        if (value instanceof Long) {
            return (Long)value;
        }

        if (value instanceof String) {
            if (((String) value).isEmpty()) {
                return defaultValue;
            }

            return Long.parseLong((String)value);
        }

        return defaultValue;
    }

    public static Integer parseInteger(Object value, Integer defaultValue) {
        if (value == null) {
            return null;
        }

        if (value instanceof Integer) {
            return (Integer)value;
        }

        if (value instanceof Long) {
            return ((Long) value).intValue();
        }

        if (value instanceof String) {
            if (((String) value).isEmpty()) {
                return defaultValue;
            }

            return Integer.parseInt((String)value);
        }

        return defaultValue;
    }

    public static String parseString(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof String) {
            return (String)value;
        }

        return value.toString();
    }
}
