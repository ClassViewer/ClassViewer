package org.glavo.viewer.util;

import javafx.scene.Node;
import javafx.scene.text.Font;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FontUtils {
    private static final Pattern fontFamily = Pattern.compile("-fx-font-family:\\s*\"[^\"]+\"\\s*;");

    private FontUtils() {
    }


    public static Font uiFont = Font.font("Dialog", 15);

    public static Font textFont = Font.loadFont("Monospaced", 15);

    public static void setUIFont(Node node) {
        setFont(node, uiFont);
    }

    public static String setUIFont(String style) {
        return setFont(style, uiFont);
    }

    public static void setTextFont(Node node) {
        setFont(node, textFont);
    }

    public static String setFont(String style, Font font) {
        return setFont(style != null ? style : "", font.getFamily());
    }

    public static String setFont(String style, String font) {
        Matcher ans = fontFamily.matcher(style);
        if (ans.find()) {
            return ans.replaceFirst("-fx-font-family: \"" + font + "\";");
        } else {
            return (style + "-fx-font-family: \"" + font + "\";");
        }
    }

    public static void setFont(Node node, Font font) {
        node.setStyle(setFont(node.getStyle(), font));
    }

    public static void setFont(Node node, String name) {
        node.setStyle(setFont(node.getStyle(), name));
    }

    public static void initUiFont() {
        List<String> fonts = Font.getFamilies();
        if (fonts.contains("PingFang SC")) {
            uiFont = Font.font("PingFang SC", 15);
        } else if (fonts.contains("Microsoft YaHei UI")) {
            uiFont = Font.font("Microsoft YaHei UI", 15);
        } else if (fonts.contains("Ubuntu")) {
            uiFont = Font.font("Ubuntu", 15);
        } else if (fonts.contains("Segoe UI")) {
            uiFont = Font.font("Segoe UI", 15);
        }
    }

    public static void initTextFont() {
        List<String> fonts = Font.getFamilies();
        if (fonts.contains("Consolas")) {
            textFont = Font.font("Consolas", 15);
        } else if (fonts.contains("Source Code Pro")) {
            textFont = Font.font("Source Code Pro", 15);
        } else if (fonts.contains("Fira Code")) {
            textFont = Font.font("Fira Code", 15);
        } else if (fonts.contains("DejaVu Sans Mono")) {
            textFont = Font.font("DejaVu Sans Mono", 15);
        }
    }

    public static Font getUiFont() {
        return uiFont;
    }

    public static void setUiFont(Font uiFont) {
        FontUtils.uiFont = uiFont;
    }

    public static Font getTextFont() {
        return textFont;
    }

    public static void setTextFont(Font textFont) {
        FontUtils.textFont = textFont;
    }
}
