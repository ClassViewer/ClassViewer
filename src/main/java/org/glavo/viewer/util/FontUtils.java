package org.glavo.viewer.util;

import javafx.scene.text.Font;

public class FontUtils {
    private FontUtils() {
    }

    static {
        Log.info("Load fonts");
    }

    public static Font uiFont = Font.loadFont(FontUtils.class.getResourceAsStream("/fonts/UI.ttf"), 15);

    public static Font textFont = Font.loadFont(FontUtils.class.getResourceAsStream("/fonts/Text.ttf"), 15);

    static {
        Log.debug("UIFont=" + uiFont);
        Log.debug("TextFont=" + textFont);
    }

    public static void init() {

    }
}
