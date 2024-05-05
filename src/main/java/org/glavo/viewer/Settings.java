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

import org.glavo.viewer.util.FontInfo;
import org.jetbrains.annotations.NotNull;
import org.tomlj.Toml;
import org.tomlj.TomlParseError;
import org.tomlj.TomlParseResult;
import org.tomlj.TomlTable;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Function;

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

    private static TomlTable get() {
        if (!loaded) {
            throw new IllegalStateException("Settings are not loaded");
        }
        return settings;
    }

    public static <T> Optional<T> get(Key<T, ?> key) {
        TomlTable tomlTable = get();
        if (tomlTable == null) {
            return Optional.empty();
        }

        try {
            TomlTable category = tomlTable.getTable(key.category.getCategory());
            if (category == null) {
                return Optional.empty();
            }

            Object value = category.get(key.name);

            if (value == null) {
                return Optional.empty();
            }
            return key.doConvert(value);
        } catch (Throwable e) {
            LOGGER.warning("Failed to read settings", e);
            return Optional.empty();
        }
    }

    public enum Category {
        UI;

        private final String category;

        Category() {
            this.category = name();
        }

        Category(String category) {
            this.category = category;
        }

        public String getCategory() {
            return category;
        }
    }

    public record Key<T, U>(Class<U> tomlType, Category category, String name,
                            Function<@NotNull U, @NotNull T> converter) {
        public static <T> Key<T, TomlTable> ofTable(Category category, String name, Function<@NotNull TomlTable, @NotNull T> converter) {
            return new Key<>(TomlTable.class, category, name, converter);
        }

        public static <T> Key<T, String> ofString(Category category, String name, Function<@NotNull String, @NotNull T> converter) {
            return new Key<>(String.class, category, name, converter);
        }

        Optional<T> doConvert(@NotNull Object object) {
            return Optional.of(converter.apply(tomlType.cast(object)));
        }

        @Override
        public String toString() {
            return category + "." + name;
        }
    }

    private static final Function<TomlTable, FontInfo> FONT_CONVERTER = table -> {
        String name = table.getString("name");
        Double size = table.getDouble("size");
        String style = table.getString("style");

        return new FontInfo(name, size, style);
    };

    public static final Key<FontInfo, TomlTable> UI_FONT = Key.ofTable(Category.UI, "font", FONT_CONVERTER);
}
