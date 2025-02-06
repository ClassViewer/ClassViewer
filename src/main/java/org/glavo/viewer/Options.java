/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 Glavo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.glavo.viewer;

import javafx.scene.text.Font;
import org.glavo.viewer.util.FontUtils;
import org.glavo.viewer.logging.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;

public final class Options {
    public static boolean color = false;
    public static boolean debug = false;
    public static String skin = null;

    public static List<Properties> properties = new ArrayList<>();

    public static void init() {
        if (Files.exists(Metadata.VIEWER_DIRECTORY.resolve("viewer.properties"))) {
            Log.info("Load Properties file: " + Metadata.VIEWER_DIRECTORY.resolve("viewer.properties"));
            try (InputStream is = Files.newInputStream(Metadata.VIEWER_DIRECTORY.resolve("viewer.properties"))) {
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

        Log.info("loading finished");
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

        if (defined("viewer.skin")) {
            if (get("viewer.skin").equalsIgnoreCase("CASPIAN")) {
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
