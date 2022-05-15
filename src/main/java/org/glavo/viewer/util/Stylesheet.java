package org.glavo.viewer.util;

import javafx.scene.text.Font;
import kala.template.TemplateEngine;
import org.glavo.viewer.Config;
import org.glavo.viewer.Main;
import org.glavo.viewer.resources.Resources;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.logging.Level;

import static org.glavo.viewer.util.Logging.LOGGER;

public final class Stylesheet {
    private static final String STYLE_TEMPLATE = "/org/glavo/viewer/resources/style.css";
    private static final double DEFAULT_FONT_SIZE = 16;

    public static String[] getStylesheets() {
        Config config = Config.getConfig();

        try {
            File cssFile = File.createTempFile("viewer-", ".css");
            cssFile.deleteOnExit();

            HashMap<String, Object> table = new HashMap<>();
            String uiFontFamily;
            double uiFontSize;

            String textFontFamily;
            double textFontSize;

            if ((uiFontFamily = config.getUIFontFamily()) == null) uiFontFamily = Font.getDefault().getFamily();
            if ((uiFontSize = config.getUIFontSize()) <= 0) uiFontSize = Font.getDefault().getSize();

            if ((textFontFamily = config.getTextFontFamily()) == null) {
                List<String> families = Font.getFamilies();

                String[] defaultTextFontFamilies = {
                        "Consolas",
                        "Source Code Pro",
                        "Fira Code",
                        "Ubuntu Mono",
                        "JetBrains Mono",
                        "DejaVu Sans Mono"
                };

                for (String family : defaultTextFontFamilies) {
                    if (families.contains(family)) {
                        textFontFamily = family;
                        break;
                    }
                }

                if (textFontFamily == null) textFontFamily = "Monospaced";
            }
            if ((textFontSize = config.getTextFontSize()) <= 0) textFontSize = Double.max(16, uiFontSize);

            LOGGER.config("UI Font Family: " + uiFontFamily);
            LOGGER.config("UI Font Size: " + uiFontSize);
            LOGGER.config("Text Font Family: " + textFontFamily);
            LOGGER.config("Text Font Size: " + textFontSize);

            table.put("ui-font-family", uiFontFamily);
            table.put("ui-font-size", uiFontSize);
            table.put("text-font-family", textFontFamily);
            table.put("text-font-size", textFontSize);

            //noinspection ConstantConditions
            try (Reader input = new InputStreamReader(Main.class.getResourceAsStream(STYLE_TEMPLATE), StandardCharsets.UTF_8);
                 Writer output = Files.newBufferedWriter(cssFile.toPath())) {
                TemplateEngine.getDefault()
                        .process(input, output, table);
            }

            LOGGER.info("Stylesheet File: " + cssFile);
            return new String[]{cssFile.toURI().toString()};
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to initialize stylesheet", e);
            return StringUtils.EMPTY_ARRAY;
        }

    }

    public static final List<String> CODE_KEYWORD_CLASSES = Collections.singletonList("code-keyword");
    public static final List<String> CODE_TERMINATOR_CLASSES = Collections.singletonList("code-terminator");
    public static final List<String> CODE_DELIMITER_CLASSES = Collections.singletonList("code-delimiter");
    public static final List<String> CODE_BRACKETS_CLASSES = Collections.singletonList("code-brackets");
    public static final List<String> CODE_OPERATOR_CLASSES = Collections.singletonList("code-operator");
    public static final List<String> CODE_STRING_CLASSES = Collections.singletonList("code-string");
    public static final List<String> CODE_COMMENT_CLASSES = Collections.singletonList("code-comment");
    public static final List<String> CODE_NUMBER_CLASSES = Collections.singletonList("code-number");
    public static final List<String> CODE_PROPERTY_KEY = Collections.singletonList("code-property-key");

    public static String getCodeStylesheet() {
        return Resources.class.getResource("code.css").toExternalForm();
    }
}
