package org.glavo.viewer.util;

import kala.collection.base.GenericArrays;

public final class StringUtils {
    private StringUtils() {
    }

    public static final String[] EMPTY_ARRAY = new String[0];

    public static String[] spiltPath(String path) {
        String[] res = path.split("[/\\\\]");

        return GenericArrays.anyMatch(res, String::isEmpty)
                ? GenericArrays.filterNot(res, String::isEmpty)
                : res;
    }
}
