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

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.glavo.viewer.file.TypedVirtualFile;
import org.glavo.viewer.ui.Schedulers;
import org.glavo.viewer.util.FileUtils;
import org.glavo.viewer.util.WindowDimension;
import org.hildan.fxgson.FxGson;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

import static org.glavo.viewer.util.logging.Logger.LOGGER;

public final class Config {
    private static final Gson GSON = FxGson.coreBuilder()
            .setPrettyPrinting()
            .create();

    public static final int RECENT_FILES_LIMIT = 20;

    private transient Path path = null;
    @SuppressWarnings("FieldCanBeLocal")
    private transient FileLock lock;

    private transient boolean needToSaveOnExit = false;
    private transient boolean hasUnknownProperties = false;

    @SaveOnExit
    @SerializedName("windowSize")
    private final ObjectProperty<WindowDimension> windowSize = new SimpleObjectProperty<>();

    @SaveOnExit
    @SerializedName("dividerPosition")
    private final DoubleProperty dividerPosition = new SimpleDoubleProperty();

    @SerializedName("uiFontFamily")
    private final StringProperty uiFontFamily = new SimpleStringProperty();

    @SerializedName("uiFontSize")
    private final DoubleProperty uiFontSize = new SimpleDoubleProperty();

    @SerializedName("textFontFamily")
    private final StringProperty textFontFamily = new SimpleStringProperty();

    @SerializedName("textFontSize")
    private final DoubleProperty textFontSize = new SimpleDoubleProperty();

    @SaveOnExit
    @SerializedName("recentFiles")
    private transient final ObservableList<TypedVirtualFile> recentFiles = FXCollections.observableArrayList(); // TODO: transient

    private static Config config;

    public Config() {
    }

    public static void load() {
        config = loadFrom(Options.getOptions().home().resolve("config.json"));
    }

    public static Config getConfig() {
        return config;
    }

    private static Config loadFrom(Path path) {
        if (Files.notExists(path)) {
            LOGGER.info("Config file does not exist");
            Config config = new Config();
            config.associationTo(path);
            return config;
        }
        LOGGER.info("Load config from '" + path + "'");

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            Config res = GSON.fromJson(reader, Config.class);
            if (res.hasUnknownProperties) {
                LOGGER.warning("Open configuration file in read-only mode due to unknown keys");
            }
            res.associationTo(path);
            return res;
        } catch (Throwable ex) {
            LOGGER.warning("Failed to read configuration", ex);
            return new Config();
        }
    }

    private void associationTo(Path path) {
        this.path = path;
        if (path == null || this.hasUnknownProperties) {
            return;
        }

        Path lockFile = path.resolveSibling(path.getFileName() + ".lock");

        FileChannel channel = null;
        FileLock lock = null;
        try {
            channel = FileChannel.open(lockFile, StandardOpenOption.WRITE, StandardOpenOption.CREATE);

            for (int retry = 0; retry < 5; retry++) {
                lock = channel.tryLock();

                if (lock != null) {
                    break;
                }

                Thread.sleep(50);
            }
        } catch (Throwable ex) {
            LOGGER.warning("Failed when trying to lock the configuration file", ex);
            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException ignored) {
                }
            }
        }

        if (lock == null) {
            return;
        }

        this.lock = lock;

        for (Field field : this.getClass().getFields()) {
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers)) {
                continue;
            }

            field.setAccessible(true);
            try {
                if (!(field.get(this) instanceof Observable observable)) {
                    throw new AssertionError("Field " + field + " not observable");
                }

                if (field.getAnnotation(SaveOnExit.class) != null) {
                    observable.addListener(o -> needToSaveOnExit = true);
                } else {
                    observable.addListener(o -> save());
                }
            } catch (IllegalAccessException e) {
                throw new AssertionError(e);
            }
        }
    }

    public void ignored(String key, Object value) {
        LOGGER.warning("Unknown key '" + key + "' in the configuration file");
        hasUnknownProperties = true;
    }

    public void save() {
        if (path != null) {
            Schedulers.io().execute(() -> {
                try {
                    FileUtils.save(path, writer -> {
                        GSON.toJson(Config.this, writer);
                        LOGGER.info("Save config");
                    });
                } catch (IOException ex) {
                    LOGGER.warning("Failed to save config", ex);
                }
            });
        }
    }

    public boolean isNeedToSaveOnExit() {
        return needToSaveOnExit;
    }

    public ObjectProperty<WindowDimension> windowSizeProperty() {
        return windowSize;
    }

    public WindowDimension getWindowSize() {
        return windowSize.get();
    }

    public void setWindowSize(WindowDimension windowSizeProperty) {
        this.windowSize.set(windowSizeProperty);
    }

    public DoubleProperty dividerPositionProperty() {
        return dividerPosition;
    }

    public double getDividerPosition() {
        return dividerPosition.get();
    }

    public void setDividerPosition(double dividerPosition) {
        this.dividerPosition.set(dividerPosition);
    }

    public StringProperty uiFontFamilyProperty() {
        return uiFontFamily;
    }

    @SerializedName("uiFontFamily")
    public String getUIFontFamily() {
        return uiFontFamily.get();
    }

    public void setUIFontFamily(String uiFontProperty) {
        this.uiFontFamily.set(uiFontProperty);
    }

    public DoubleProperty uiFontSizeProperty() {
        return uiFontSize;
    }

    @SerializedName("uiFontSize")
    public double getUIFontSize() {
        return uiFontSize.get();
    }

    public void setUIFontSize(double uiFontSizeProperty) {
        this.uiFontSize.set(uiFontSizeProperty);
    }

    public StringProperty textFontFamilyProperty() {
        return textFontFamily;
    }

    public String getTextFontFamily() {
        return textFontFamily.get();
    }

    public void setTextFontFamily(String textFontProperty) {
        this.textFontFamily.set(textFontProperty);
    }

    public DoubleProperty textFontSizeProperty() {
        return textFontSize;
    }

    public double getTextFontSize() {
        return textFontSize.get();
    }

    public void setTextFontSize(double textFontSizeProperty) {
        this.textFontSize.set(textFontSizeProperty);
    }

    public ObservableList<TypedVirtualFile> getRecentFiles() {
        return recentFiles;
    }

    public void setRecentFiles(List<TypedVirtualFile> files) {
        getRecentFiles().setAll(files);
    }

    public void addRecentFile(TypedVirtualFile path) {
        ObservableList<TypedVirtualFile> files = getRecentFiles();
        files.remove(path);
        if (files.size() == RECENT_FILES_LIMIT) {
            files.removeLast();
        }
        files.addFirst(path);
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface SaveOnExit {
    }
}
