package org.glavo.viewer.gui;

import javafx.scene.text.Font;
import org.glavo.viewer.util.FontUtils;
import org.glavo.viewer.util.Log;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public final class Options {
    public static boolean color = true;
    public static boolean debug = false;

    public static void init() {
        init(System.getProperties());
    }

    public static void init(Properties... properties) {
        if (properties != null)
            init(Arrays.asList(properties));
        else
            init(Collections.singletonList(new Properties()));
    }

    public static void init(List<Properties> properties) {
        if (properties == null) {
            properties = Collections.emptyList();
        }

        Options.debug = defined(properties, "viewer.debug");

        boolean color = defined(properties, "viewer.color");
        if (System.getProperty("os.name", "windows").toLowerCase().contains("win")) {
            Options.color = false;
        }
        if (color) {
            Options.color = !Options.color;
        }
        Log.debug("Options.color=" + Options.color);

        String uiFont = get(properties, "viewer.fonts.ui");
        if (uiFont != null) {
            FontUtils.uiFont =  Font.font(uiFont, 15);
        } else {
            FontUtils.initUiFont();
        }
        Log.debug("UIFont=" + FontUtils.uiFont);

        String textFont = get(properties, "viewer.fonts.text");
        if (textFont != null) {
            FontUtils.textFont =  Font.font(textFont, 15);
        } else {
            FontUtils.initTextFont();
        }
        Log.debug("TextFont=" + FontUtils.textFont);
    }

    private static String get(List<Properties> properties, String key) {
        for (Properties property : properties) {
            String value = property.getProperty(key, null);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    private static String get(List<Properties> properties, String key, String defaultValue) {
        for (Properties property : properties) {
            String value = property.getProperty(key, null);
            if (value != null) {
                return value;
            }
        }
        return defaultValue;
    }

    private static boolean defined(List<Properties> properties, String key) {
        for (Properties property : properties) {
            String value = property.getProperty(key, null);
            if (value != null) {
                return true;
            }
        }

        return false;
    }

    private static Integer getInt(List<Properties> properties, String key) {
        for (Properties property : properties) {
            String value = property.getProperty(key, null);
            if (value != null) {
                value = value.trim();
                try {
                    return Integer.valueOf(value);
                } catch (Exception ignored) {
                }
            }
        }
        return null;
    }
}
