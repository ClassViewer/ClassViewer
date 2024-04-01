package org.glavo.viewer.resources;

import java.util.ResourceBundle;

public final class I18N {
    private static final ResourceBundle resources = ResourceBundle.getBundle(I18N.class.getName());

    public static String getString(String key) {
        return resources.getString(key);
    }
}
