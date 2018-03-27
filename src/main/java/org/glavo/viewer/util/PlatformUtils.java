package org.glavo.viewer.util;

public class PlatformUtils {
    private static String getOS() {
        return System.getProperty("os.name");
    }

    public static boolean isOSX() {
        final String os = getOS();
        return os != null && os.startsWith("Mac");
    }
}
