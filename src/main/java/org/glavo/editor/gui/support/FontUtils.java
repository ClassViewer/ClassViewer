package org.glavo.editor.gui.support;

import javafx.scene.text.Font;
import org.glavo.editor.helper.Log;

public class FontUtils {
    private FontUtils() {
    }

    static {
        Log.log("loading fonts...");
    }


    public static Font uiFont = Font.loadFont(FontUtils.class.getResourceAsStream("/UI.ttf"), 12);

    public static Font textFont = Font.loadFont(FontUtils.class.getResourceAsStream("/Text.ttf"), 15);
}
