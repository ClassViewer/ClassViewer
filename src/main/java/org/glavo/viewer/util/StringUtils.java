package org.glavo.viewer.util;

public final class StringUtils {
    private StringUtils() {
    }

    public static final String[] EMPTY_ARRAY = new String[0];

    public static String[] spiltPath(String path) {
        return path.split("[/\\\\]");
    }
}
