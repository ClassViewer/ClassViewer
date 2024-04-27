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
package org.glavo.viewer;

import org.tomlj.Toml;
import org.tomlj.TomlParseError;
import org.tomlj.TomlParseResult;
import org.tomlj.TomlTable;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.glavo.viewer.util.logging.Logger.LOGGER;

public final class Settings {
    private static boolean loaded = false;
    private static TomlTable settings;

    public static void load(Path home) {
        if (loaded) {
            throw new IllegalStateException("Settings are already loaded");
        }

        loaded = true;

        Path settingsFile = home.resolve("settings").resolve("settings.toml");

        if (Files.isRegularFile(settingsFile)) {
            try (BufferedReader reader = Files.newBufferedReader(settingsFile)) {
                TomlParseResult toml = Toml.parse(reader);

                if (toml.hasErrors()) {
                    IOException exception = new IOException("Failed to parse settings file");
                    for (TomlParseError error : toml.errors()) {
                        exception.addSuppressed(error);
                    }
                    throw exception;
                }

                settings = toml;
            } catch (IOException e) {
                LOGGER.warning("Failed to read settings", e);
            }
        }
    }

    public static TomlTable get() {
        if (!loaded) {
            throw new IllegalStateException("Settings are not loaded");
        }
        return settings;
    }
}
