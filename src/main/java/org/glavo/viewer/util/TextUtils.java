package org.glavo.viewer.util;

import javafx.geometry.Insets;
import javafx.scene.control.Hyperlink;

public final class TextUtils {
    public static Hyperlink createHyperlinkWithoutPadding() {
        Hyperlink link = new Hyperlink();
        link.setPadding(Insets.EMPTY);
        return link;
    }

    public static Hyperlink createHyperlinkWithoutPadding(String text) {
        Hyperlink link = new Hyperlink(text);
        link.setPadding(Insets.EMPTY);
        return link;
    }
}
