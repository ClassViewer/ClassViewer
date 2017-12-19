package org.glavo.editor.helper;

import javafx.scene.text.Font;

public class FontHelper {
    private FontHelper() {
    }

    static {
        Log.log("loading fonts...");
    }


    public static Font uiFont = Font.loadFont(FontHelper.class.getResourceAsStream("/UI.ttf"), 12);

    public static Font textFont = Font.loadFont(FontHelper.class.getResourceAsStream("/Text.ttf"), 14);
}
