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

import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class PathUtils {
    public static @Nullable List<String> splitPath(String path) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        if (path.isEmpty()) {
            return List.of();
        }

        String[] split = path.split("/");
        for (String s : split) {
            if (s.isEmpty()) {
                return null;
            }
        }
        return List.of(split);
    }

    public static String getFileName(String path) {
        int idx = path.lastIndexOf('/');
        if (idx < 0) {
            return path;
        } else {
            return path.substring(idx + 1);
        }
    }

    private PathUtils() {
    }
}
