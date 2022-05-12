package org.glavo.viewer.util;

import java.util.Arrays;

public final class StringUtils {
    private StringUtils() {
    }

    public static final String[] EMPTY_ARRAY = new String[0];

    public static String[] spiltPath(String path) {
        String[] res = path.split("[/\\\\]");
        if (path.startsWith("/")) {
            return Arrays.copyOfRange(res, 1, res.length);
        } else {
            return res;
        }
    }
}
