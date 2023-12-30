package org.glavo.viewer.resources;

import java.util.ResourceBundle;

public class I18N {
    private static final ResourceBundle resources = ResourceBundle.getBundle("org.glavo.viewer.resources.I18N");

    public static String getString(String key) {
        return resources.getString(key);
    }

    public static String getString(String key, Object... args) {
        return String.format(resources.getString(key), args);
    }
}
