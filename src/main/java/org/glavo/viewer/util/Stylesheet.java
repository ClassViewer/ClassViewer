/*
 * Copyright 2024 Glavo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.glavo.viewer.util;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.text.Font;
import kala.template.TemplateEngine;
import org.glavo.viewer.Config;
import org.glavo.viewer.annotation.FXThread;
import org.glavo.viewer.resources.Resources;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

import static org.glavo.viewer.util.logging.Logger.LOGGER;

public final class Stylesheet {
    private static final String STYLE_TEMPLATE = "stylesheet/style.css";
    private static final double DEFAULT_FONT_SIZE = 16;

    private static final StringProperty stylesheet = new SimpleStringProperty();

    public static void init() {
        Config config = Config.getConfig();

        HashMap<String, Object> table = new HashMap<>();
        String uiFontFamily = config.getUIFontFamily();
        double uiFontSize = config.getUIFontSize();

        String textFontFamily = config.getTextFontFamily();
        double textFontSize = config.getTextFontSize();

        if (uiFontFamily == null) {
            uiFontFamily = Font.getDefault().getFamily();
        }
        if (uiFontSize <= 0) {
            uiFontSize = 14;
        }

        if (textFontFamily == null) {
            try (InputStream inputStream = Resources.class.getResourceAsStream("fonts/JetBrainsMono-Regular.ttf")) {
                Font font = Font.loadFont(inputStream, 0);
                if (font != null) {
                    textFontFamily = font.getFamily();
                } else {
                    LOGGER.warning("Failed to load font");
                }
            } catch (Throwable e) {
                LOGGER.warning("Failed to load font", e);
            }

            if (textFontFamily == null) {
                textFontFamily = Font.getDefault().getFamily();
            }
        }
        if (textFontSize <= 0) {
            textFontSize = Double.max(16, uiFontSize);
        }

        LOGGER.trace("UI Font Family: " + uiFontFamily);
        LOGGER.trace("UI Font Size: " + uiFontSize);
        LOGGER.trace("Text Font Family: " + textFontFamily);
        LOGGER.trace("Text Font Size: " + textFontSize);

        table.put("ui-font-family", uiFontFamily);
        table.put("ui-font-size", uiFontSize);
        table.put("text-font-family", textFontFamily);
        table.put("text-font-size", textFontSize);

        String uri;

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        //noinspection ConstantConditions
        try (Reader input = new InputStreamReader(Resources.class.getResourceAsStream(STYLE_TEMPLATE), StandardCharsets.UTF_8)) {
            try (OutputStreamWriter writer = new OutputStreamWriter(output, StandardCharsets.UTF_8)) {
                TemplateEngine.getDefault().process(input, writer, table);
            }

            uri = "data:text/css;base64," + Base64.getEncoder().encodeToString(output.toByteArray());
        } catch (IOException e) {
            LOGGER.warning("Failed to initialize stylesheet", e);
            uri = null;
        }

        stylesheet.set(uri);
    }

    @FXThread
    public static void setStylesheet(ObservableList<String> target) {
        ChangeListener<String> listener = (o, oldValue, newValue) -> {
            if (newValue != null) {
                target.setAll(newValue);
            } else {
                target.clear();
            }
        };
        listener.changed(stylesheet, null, stylesheet.getValue());
        stylesheet.addListener(new WeakChangeListener<>(listener));
    }

    public static final List<String> CODE_KEYWORD_CLASSES = List.of("code-keyword");
    public static final List<String> CODE_TERMINATOR_CLASSES = List.of("code-terminator");
    public static final List<String> CODE_DELIMITER_CLASSES = List.of("code-delimiter");
    public static final List<String> CODE_BRACKETS_CLASSES = List.of("code-brackets");
    public static final List<String> CODE_OPERATOR_CLASSES = List.of("code-operator");
    public static final List<String> CODE_STRING_CLASSES = List.of("code-string");
    public static final List<String> CODE_COMMENT_CLASSES = List.of("code-comment");
    public static final List<String> CODE_NUMBER_CLASSES = List.of("code-number");
    public static final List<String> CODE_PROPERTY_KEY = List.of("code-property-key");
}
