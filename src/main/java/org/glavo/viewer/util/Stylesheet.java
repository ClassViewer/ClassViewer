package org.glavo.viewer.util;

import kala.template.TemplateEngine;
import org.glavo.viewer.Config;
import org.glavo.viewer.Main;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.logging.Level;

import static org.glavo.viewer.util.Logging.LOGGER;

public final class Stylesheet {
    private static final String STYLE_TEMPLATE = "/org/glavo/viewer/resources/style.css.template";

    public static String[] getStylesheets() {
        Config config = Config.getConfig();

        try {
            File cssFile = File.createTempFile("viewer-", ".css");
            cssFile.deleteOnExit();

            HashMap<String, Object> table = new HashMap<>();

            table.put("ui-font-family", Optional.ofNullable(config.getUIFontFamily()).orElse("Dialog"));
            table.put("ui-font-size", config.getUIFontSize());
            table.put("text-font-family", Optional.ofNullable(config.getTextFontFamily()).orElse("Monospaced"));
            table.put("text-font-size", config.getTextFontSize());

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
        }

        return new String[0];
    }
}
