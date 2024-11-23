package org.hbin.utils;

public class CommonUtils {
    public static boolean isPrimitiveOrWrapper(Class<?> type) {
        return type.isPrimitive() || type == String.class ||
                type == Integer.class || type == Long.class ||
                type == Double.class || type == Float.class ||
                type == Boolean.class || type == Byte.class ||
                type == Short.class || type == Character.class;
    }
}
