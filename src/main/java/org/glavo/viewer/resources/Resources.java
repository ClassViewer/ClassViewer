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
package org.glavo.viewer.resources;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.net.URL;

public final class Resources {

    public static @NotNull URL getResource(String name) {
        URL resource = Resources.class.getResource(name);
        if (resource == null) {
            throw new AssertionError("Resource not found: " + name);
        }
        return resource;
    }

    public static @NotNull InputStream getResourceAsStream(String name) {
        InputStream stream = Resources.class.getResourceAsStream(name);
        if (stream == null) {
            throw new AssertionError("Resource not found: " + name);
        }
        return stream;
    }

    private Resources() {
    }
}
