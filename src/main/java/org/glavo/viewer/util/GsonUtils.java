package org.glavo.viewer.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class GsonUtils {
    public static final Gson GSON = new GsonBuilder().create();

    private GsonUtils() {
    }
}
