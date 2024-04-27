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

import javafx.scene.Node;
import javafx.scene.control.Tooltip;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.function.Function;
import java.util.regex.Pattern;

public final class StringUtils {
    private StringUtils() {
    }

    public static final String[] EMPTY_ARRAY = new String[0];

    public static final int SHORT_TEXT_THRESHOLD = 25;

    public static String[] spiltPath(String path) {
        String[] res = path.split("[/\\\\]");
        for (String element : res) {
            if (element.isEmpty()) {
                return Arrays.stream(res).filter(it -> !it.isEmpty()).toArray(String[]::new);
            }
        }

        return res;
    }

    public static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter(1024);
        try (PrintWriter writer = new PrintWriter(sw)) {
            throwable.printStackTrace(writer);
        }
        return sw.toString();
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

    private static final String[] level1 = {"#0", "#1", "#2", "#3", "#4", "#5", "#6", "#7", "#8", "#9"};
    private static final String[] level2 = {
            "#00", "#01", "#02", "#03", "#04", "#05", "#06", "#07", "#08", "#09",
            "#10", "#11", "#12", "#13", "#14", "#15", "#16", "#17", "#18", "#19",
            "#20", "#21", "#22", "#23", "#24", "#25", "#26", "#27", "#28", "#29",
            "#30", "#31", "#32", "#33", "#34", "#35", "#36", "#37", "#38", "#39",
            "#40", "#41", "#42", "#43", "#44", "#45", "#46", "#47", "#48", "#49",
            "#50", "#51", "#52", "#53", "#54", "#55", "#56", "#57", "#58", "#59",
            "#60", "#61", "#62", "#63", "#64", "#65", "#66", "#67", "#68", "#69",
            "#70", "#71", "#72", "#73", "#74", "#75", "#76", "#77", "#78", "#79",
            "#80", "#81", "#82", "#83", "#84", "#85", "#86", "#87", "#88", "#89",
            "#90", "#91", "#92", "#93", "#94", "#95", "#96", "#97", "#98", "#99"
    };
    private static final String[] level3 = new String[1000];

    public static String formatIndex(int index, int maxIndex) {
        if (maxIndex < 10) {
            return level1[index];
        }

        if (maxIndex < 100) {
            return level2[index];
        }

        if (maxIndex < 1000) {
            String res = level3[index];
            if (res == null)
                res = level3[index] = String.format("#%03d", index).intern();
            return res;
        }

        int idxWidth = String.valueOf(maxIndex).length();
        String fmtStr = "#%0" + idxWidth + "d";
        return String.format(fmtStr, index);
    }
}
