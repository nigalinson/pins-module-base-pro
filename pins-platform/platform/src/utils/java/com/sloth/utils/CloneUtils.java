package com.sloth.utils;

import java.lang.reflect.Type;

public final class CloneUtils {

    private CloneUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * Deep clone.
     *
     * @param data The data.
     * @param type The type.
     * @param <T>  The value type.
     * @return The object of cloned.
     */
    public static <T> T deepClone(final T data, final Type type) {
        try {
            return UtilsCenter.fromJson(UtilsCenter.toJson(data), type);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
