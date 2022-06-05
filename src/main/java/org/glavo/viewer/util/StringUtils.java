package org.glavo.viewer.util;

import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import kala.collection.base.GenericArrays;

import java.util.function.Function;
import java.util.regex.Pattern;

public final class StringUtils {
    private StringUtils() {
    }

    public static final String[] EMPTY_ARRAY = new String[0];

    public static final int SHORT_TEXT_THRESHOLD = 25;

    public static String[] spiltPath(String path) {
        String[] res = path.split("[/\\\\]");

        return GenericArrays.anyMatch(res, String::isEmpty)
                ? GenericArrays.filterNot(res, String::isEmpty)
                : res;
    }


    private static final Pattern newLine = Pattern.compile("[\\r\\n]");

    public static <T extends Node> T cutTextNode(String text, Function<String, T> f) {
        String shortText = cutAndAppendEllipsis(text);

        T t = f.apply(shortText);
        //noinspection StringEquality
        if (shortText != text || text.length() > StringUtils.SHORT_TEXT_THRESHOLD) {
            Tooltip.install(t, new Tooltip(text));
        }

        return t;
    }

    public static String cutAndAppendEllipsis(String str) {
        return cutAndAppendEllipsis(str, 100);
    }

    public static String cutAndAppendEllipsis(String str, int maxLength) {
        str = newLine.matcher(str).replaceAll("");

        if (str.length() <= maxLength) {
            return str;
        }

        int cutPos = maxLength - 3;
        char firstCutChar = str.charAt(cutPos);

        if (Character.isLowSurrogate(firstCutChar)) {
            return str.substring(0, cutPos - 1) + "...";
        } else {
            return str.substring(0, cutPos) + "...";
        }
    }

    public static String formatIndex(int index, int maxIndex) {
        int idxWidth = String.valueOf(maxIndex).length();
        String fmtStr = "#%0" + idxWidth + "d";
        return String.format(fmtStr, index);
    }
}
