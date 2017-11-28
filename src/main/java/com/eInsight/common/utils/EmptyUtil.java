package com.eInsight.common.utils;

import java.util.Collection;
import java.util.Map;

public class EmptyUtil {
    public static boolean isNullOrEmpty(String str) {
        return (str == null) || (str.isEmpty());
    }

    public static boolean isNullOrZero(Integer num) {
        return (num == null) || (num.intValue() == 0);
    }

    public static boolean isNullOrZero(Long num) {
        return (num == null) || (num.longValue() == 0L);
    }

    public static boolean isNullOrZero(Double num) {
        return (num == null) || (num.doubleValue() == 0.0D);
    }

    public static boolean isNullOrZero(Float num) {
        return (num == null) || (num.floatValue() == 0.0F);
    }

    public static boolean isNullOrZero(Short num) {
        return (num == null) || (num.shortValue() == 0);
    }

    public static boolean isNullOrZero(Byte num) {
        return (num == null) || (num.byteValue() == 0);
    }

    public static boolean isNullOrEmpty(Object[] byteArray) {
        return (byteArray == null) || (byteArray.length == 0);
    }

    public static boolean isNullOrEmpty(int[] byteArray) {
        return (byteArray == null) || (byteArray.length == 0);
    }

    public static boolean isNullOrEmpty(long[] byteArray) {
        return (byteArray == null) || (byteArray.length == 0);
    }

    public static boolean isNullOrEmpty(double[] byteArray) {
        return (byteArray == null) || (byteArray.length == 0);
    }

    public static boolean isNullOrEmpty(float[] byteArray) {
        return (byteArray == null) || (byteArray.length == 0);
    }

    public static boolean isNullOrEmpty(short[] byteArray) {
        return (byteArray == null) || (byteArray.length == 0);
    }

    public static boolean isNullOrEmpty(byte[] byteArray) {
        return (byteArray == null) || (byteArray.length == 0);
    }

    public static boolean isNullOrEmpty(Collection<?> collection) {
        return (collection == null) || (collection.size() == 0);
    }

    public static boolean isNullOrEmpty(Map<?, ?> map) {
        return (map == null) || (map.size() == 0);
    }
}
