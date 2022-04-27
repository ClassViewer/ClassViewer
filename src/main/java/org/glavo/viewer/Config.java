package org.glavo.viewer;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import org.glavo.viewer.util.FileUtils;
import org.glavo.viewer.util.JsonUtils;
import org.glavo.viewer.util.WindowDimension;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;

import static org.glavo.viewer.util.Logging.LOGGER;

public final class Config {
    private Path path = null;
    private FileLock lock;

    private boolean needToSaveOnExit = false;
    private boolean hasUnknownProperties = false;

    private final ObjectProperty<WindowDimension> windowSizeProperty = new SimpleObjectProperty<>();
    private final StringProperty uiFontFamilyProperty = new SimpleStringProperty();
    private final DoubleProperty uiFontSizeProperty = new SimpleDoubleProperty(-1);
    private final StringProperty textFontFamilyProperty = new SimpleStringProperty();
    private final DoubleProperty textFontSizeProperty = new SimpleDoubleProperty(-1);

    private static Config config;

    public Config() {
    }

    public Config(Path path) {
        init(path);
    }

    public static void load() {
        config = loadFrom(Options.getOptions().getHome().resolve("config.json"));
    }

    public static Config getConfig() {
        return config;
    }

    private static Config loadFrom(Path path) {
        if (Files.notExists(path)) {
            LOGGER.info("Config file does not exist");
            return new Config(path);
        }
        LOGGER.info("Load config from '" + path + "'");


        try (BufferedReader reader = Files.newBufferedReader(path)) {
            Config res = JsonUtils.MAPPER.readValue(reader, Config.class);
            if (res.hasUnknownProperties) {
                LOGGER.warning("Open configuration file in read-only mode due to unknown keys");
            }
            res.init(path);
            return res;
        } catch (Throwable ex) {
            LOGGER.log(Level.WARNING, "Failed to read configuration", ex);
            return new Config(null);
        }
    }

    private void init(Path path) {
        this.path = path;
        if (path == null || this.hasUnknownProperties) {
            return;
        }

        Path lockFile = path.resolveSibling(path.getFileName() + ".lock");

        FileChannel channel = null;
        FileLock lock = null;
        try {
            channel = FileChannel.open(lockFile, StandardOpenOption.WRITE, StandardOpenOption.CREATE);

            int retry = 0;

            while (true) {
                lock = channel.tryLock();

                if (lock != null) {
                    break;
                }

                if (retry++ > 5) {
                    LOGGER.info("Failed to acquire file lock on configuration file, open it in read-only mode");
                    break;
                }

                //noinspection BusyWait
                Thread.sleep(10);
            }
        } catch (Throwable ex) {
            LOGGER.log(Level.WARNING, "Failed when trying to lock the configuration file", ex);
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

        needToSaveOnExit(windowSizeProperty);
        needToSave(uiFontFamilyProperty);
        needToSave(uiFontSizeProperty);
        needToSave(textFontFamilyProperty);
        needToSave(textFontSizeProperty);
    }

    private void needToSave(Observable value) {
        value.addListener(o -> save());
    }

    private void needToSaveOnExit(ObservableValue<?> value) {
        value.addListener(o -> needToSaveOnExit = true);
    }

    @JsonAnySetter
    public void ignored(String key, Object value) {
        LOGGER.warning("Unknown key '" + key + "' in the configuration file");
        hasUnknownProperties = true;
    }

    public void save() {
        if (path != null) {
            FileUtils.ioThread.submit(() -> {
                try {
                    FileUtils.save(path, writer -> {
                        JsonUtils.MAPPER.writeValue(writer, Config.this);
                        LOGGER.info("Save config");
                    });
                } catch (IOException ex) {
                    LOGGER.log(Level.WARNING, "Failed to save config", ex);
                }
            });
        }
    }

    @JsonIgnore
    public boolean isNeedToSaveOnExit() {
        return needToSaveOnExit;
    }

    public ObjectProperty<WindowDimension> windowSizeProperty() {
        return windowSizeProperty;
    }

    public WindowDimension getWindowSize() {
        return windowSizeProperty.get();
    }

    public void setWindowSize(WindowDimension windowSizeProperty) {
        this.windowSizeProperty.set(windowSizeProperty);
    }

    public StringProperty uiFontFamilyProperty() {
        return uiFontFamilyProperty;
    }

    @JsonProperty("uiFontFamily")
    public String getUIFontFamily() {
        return uiFontFamilyProperty.get();
    }

    public void setUIFontFamily(String uiFontProperty) {
        this.uiFontFamilyProperty.set(uiFontProperty);
    }

    public DoubleProperty uiFontSizeProperty() {
        return uiFontSizeProperty;
    }

    @JsonProperty("uiFontSize")
    public double getUIFontSize() {
        return uiFontSizeProperty.get();
    }

    public void setUIFontSize(double uiFontSizeProperty) {
        this.uiFontSizeProperty.set(uiFontSizeProperty);
    }

    public StringProperty textFontFamilyProperty() {
        return textFontFamilyProperty;
    }

    public String getTextFontFamily() {
        return textFontFamilyProperty.get();
    }

    public void setTextFontFamily(String textFontProperty) {
        this.textFontFamilyProperty.set(textFontProperty);
    }

    public DoubleProperty textFontSizeProperty() {
        return textFontSizeProperty;
    }

    public double getTextFontSize() {
        return textFontSizeProperty.get();
    }

    public void setTextFontSize(double textFontSizeProperty) {
        this.textFontSizeProperty.set(textFontSizeProperty);
    }
}
