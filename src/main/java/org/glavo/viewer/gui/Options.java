package org.glavo.viewer.gui;

import javafx.scene.text.Font;
import org.glavo.viewer.util.FontUtils;
import org.glavo.viewer.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public final class Options {
    public static boolean color = true;
    public static boolean debug = false;
    public static boolean useSystemTilteBar = true;
    public static String skin = null;

    public static Path path = Paths.get(System.getProperty("user.home")).resolve(".viewer");
    public static List<Properties> properties = new ArrayList<>();


    public static void init() {
        String p = System.getProperty("viewer.path");
        if (p != null) {
            path = Paths.get(p);
        }
        try {
            if (Files.notExists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            Log.error(e);
        }
        if (Files.exists(path.resolve("viewer.properties"))) {
            Log.info("Load Properties file: " + path.resolve("viewer.properties"));
            try (InputStream is = Files.newInputStream(path.resolve("viewer.properties"))) {
                Properties ps = new Properties();
                ps.load(is);
                init(System.getProperties(), ps);
            } catch (IOException e) {
                Log.error(e);
                init(System.getProperties());
            }
        } else {
            Log.info("Not found Properties file");
            init(System.getProperties());
        }

        RecentFiles.init();
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
        Options.properties = properties;

        Options.debug = defined("viewer.debug");
        Log.setting("viewer.debug", debug);

        boolean color = defined("viewer.color");
        if (System.getProperty("os.name", "windows").toLowerCase().contains("win")) {
            Options.color = false;
        }
        if (color) {
            Options.color = !Options.color;
        }
        Log.setting("viewer.color", Options.color);


        Double uiFontSize = getDouble("viewer.fonts.ui.size");
        if (uiFontSize != null) {
            FontUtils.uiFontSize = uiFontSize;
        }

        String uiFont = get("viewer.fonts.ui");
        if (uiFont != null) {
            FontUtils.uiFont = Font.font(uiFont, FontUtils.uiFontSize);
        } else {
            FontUtils.initUiFont();
        }
        Log.setting("viewer.fonts.ui", FontUtils.uiFont);


        Double textFontSize = getDouble("viewer.fonts.text.size");
        if (textFontSize != null) {
            FontUtils.textFontSize = textFontSize;
        }

        String textFont = get("viewer.fonts.text");
        if (textFont != null) {
            FontUtils.textFont = Font.font(textFont, FontUtils.textFontSize);
        } else {
            FontUtils.initTextFont();
        }
        Log.setting("viewer.fonts.text", FontUtils.textFont);

        String locale = get("viewer.locale");
        if (locale != null) {
            Locale.setDefault(Locale.forLanguageTag(locale));
        }
        Log.setting("viewer.locale", Locale.getDefault());

        if (defined("viewer.disableSystemTitleBar")) {
            useSystemTilteBar = false;
        }
        Log.setting("viewer.disableSystemTitleBar", !useSystemTilteBar);

        if (defined("viewer.skin")) {
            if (get("viewer.skin").toUpperCase().equals("CASPIAN")) {
                skin = "CASPIAN";
            } else {
                skin = "MODENA";
            }
        } else {
            skin = "MODENA";
        }
        Log.setting("viewer.skin", skin);
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

    private static String get(String key) {
        return get(properties, key);
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

    private static String get(String key, String defaultValue) {
        return get(properties, key, defaultValue);
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

    private static boolean defined(String key) {
        return defined(properties, key);
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

    private static Integer getInt(String key) {
        return getInt(properties, key);
    }

    private static Double getDouble(List<Properties> properties, String key) {
        for (Properties property : properties) {
            String value = property.getProperty(key, null);
            if (value != null) {
                value = value.trim();
                try {
                    return Double.valueOf(value);
                } catch (Exception ignored) {
                }
            }
        }
        return null;
    }

    private static Double getDouble(String key) {
        return getDouble(properties, key);
    }
}
