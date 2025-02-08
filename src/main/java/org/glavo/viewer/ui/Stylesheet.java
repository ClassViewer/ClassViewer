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
package org.glavo.viewer.ui;

import javafx.scene.Scene;
import javafx.scene.text.Font;
import org.glavo.viewer.annotation.FXThread;
import org.glavo.viewer.resources.Resources;
import org.glavo.viewer.util.PropertiesUtils;
import org.glavo.viewer.util.logging.Log;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

public final class Stylesheet {
    private static final double DEFAULT_FONT_SIZE = 14;

    private static final String[] stylesheets = new String[2];

    static {
        stylesheets[0] = getDefaultCustom();
        stylesheets[1] = Resources.getResource("css/root.css").toExternalForm();
    }

    private static String getDefaultCustom() {
        String uiFontFamily = PropertiesUtils.getString("viewer.fonts.ui", "System");
        double uiFontSize = PropertiesUtils.getDouble("viewer.fonts.ui.size", DEFAULT_FONT_SIZE);

        String textFontFamily = PropertiesUtils.getString("viewer.fonts.text");
        double textFontSize = PropertiesUtils.getDouble("viewer.fonts.text.size", DEFAULT_FONT_SIZE);

        if (textFontFamily == null) {
            List<String> fonts = Font.getFamilies();

            for (String font : new String[]{
                    "Consolas", "Source Code Pro", "Fira Code", "DejaVu Sans Mono"
            }) {
                if (fonts.contains(font)) {
                    textFontFamily = font;
                    break;
                }
            }

            if (textFontFamily == null) {
                textFontFamily = "Monospaced";
            }
        }

        Log.info("UI Font Family: " + uiFontFamily);
        Log.info("UI Font Size: " + uiFontSize);
        Log.info("Text Font Family: " + textFontFamily);
        Log.info("Text Font Size: " + textFontSize);

        var stylesheetBuilder = new StringBuilder();

        stylesheetBuilder.append(".root {");
        stylesheetBuilder.append("-fx-font-family: \"").append(uiFontFamily).append("\";");
        stylesheetBuilder.append("-fx-font-size: ").append(uiFontSize).append(";");
        stylesheetBuilder.append("}\n");

        stylesheetBuilder.append(".monospaced {");
        stylesheetBuilder.append("-fx-font-family: \"").append(textFontFamily).append("\";");
        stylesheetBuilder.append("-fx-font-size: ").append(textFontSize).append(";");
        stylesheetBuilder.append("}\n");

        return "data:text/css;charset=UTF-8;base64," + Base64.getEncoder().encodeToString(stylesheetBuilder.toString().getBytes(StandardCharsets.UTF_8));
    }

    @FXThread
    public static void setStylesheet(Scene target) {
        target.getStylesheets().addAll(stylesheets);
    }

}
