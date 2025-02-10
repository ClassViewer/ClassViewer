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

import org.glavo.viewer.resources.Resources;
import org.glavo.viewer.util.logging.Log;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

public final class Metadata {

    public static final Path VIEWER_DIRECTORY;
    private static final Properties PROPERTIES = new Properties();

    static {
        String viewerHome = System.getProperty("viewer.home", System.getProperty("viewer.path"));
        if (viewerHome == null) {
            VIEWER_DIRECTORY = Path.of(System.getProperty("user.home"), ".viewer");
        } else {
            VIEWER_DIRECTORY = Path.of(viewerHome).toAbsolutePath().normalize();
        }

        try (var reader = Resources.getResourceAsReader("metadata.properties")) {
            PROPERTIES.load(reader);
        } catch (IOException e) {
            Log.warning("Failed to load metadata", e);
        }
    }

    public static String getAttribute(String key, String defaultValue) {
        return PROPERTIES.getProperty(key, defaultValue);
    }

    private Metadata() {
    }
}
