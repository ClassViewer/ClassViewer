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
package org.glavo.viewer.util;

import org.glavo.viewer.util.logging.Log;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Properties;

public final class PropertiesUtils {

    public static void loadProperties(Path propertiesFile) {
        if (Files.exists(propertiesFile)) {
            Log.info("Load Properties file: " + propertiesFile);

            try (var reader = Files.newBufferedReader(propertiesFile)) {
                Properties properties = new Properties();
                properties.load(reader);
                System.getProperties().putAll(properties);
            } catch (IOException e) {
                Log.warning("Failed to load properties", e);
            }
        }

        String locale = getString("viewer.locale");
        if (locale != null) {
            Locale.setDefault(Locale.forLanguageTag(locale));
        }
        Log.info("Locale: " + Locale.getDefault());
    }

    public static String getString(String key) {
        return System.getProperty(key);
    }

    public static String getString(String key, String defaultValue) {
        return System.getProperty(key, defaultValue);
    }

    public static Double getDouble(String key) {
        String value = System.getProperty(key);
        if (value == null) {
            return null;
        }

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            Log.warning("Invalid value " + value + " for " + key);
            return null;
        }
    }

    public static double getDouble(String key, double defaultValue) {
        String value = System.getProperty(key);
        if (value == null) {
            return defaultValue;
        }

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            Log.warning("Invalid value " + value + " for " + key);
            return defaultValue;
        }
    }

    private PropertiesUtils() {
    }
}
