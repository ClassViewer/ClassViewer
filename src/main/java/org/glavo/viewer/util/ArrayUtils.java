package org.glavo.viewer.util;

import java.util.Arrays;
import java.util.Objects;

public final class ArrayUtils {
    public static boolean isPrefix(String[] values, String[] prefix) {
        if (prefix.length > values.length) {
            return false;
        }

        for (int i = 0; i < prefix.length; i++) {
            if (!Objects.equals(prefix[i], values[i])) {
                return false;
            }
        }

        return true;
    }
}
