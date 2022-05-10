package org.glavo.viewer;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.glavo.viewer.file.FilePath;
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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static org.glavo.viewer.util.Logging.LOGGER;

public final class Config {
    public static final int RECENT_FILES_LIMIT = 20;

    private Path path = null;
    private FileLock lock;

    private boolean needToSaveOnExit = false;
    private boolean hasUnknownProperties = false;

    private final ObjectProperty<WindowDimension> windowSize = new SimpleObjectProperty<>();
    private final DoubleProperty dividerPosition = new SimpleDoubleProperty(-1.0);
    private final StringProperty uiFontFamily = new SimpleStringProperty();
    private final DoubleProperty uiFontSize = new SimpleDoubleProperty(-1);
    private final StringProperty textFontFamily = new SimpleStringProperty();
    private final DoubleProperty textFontSize = new SimpleDoubleProperty(-1);

    private final ObservableList<FilePath> recentFiles = FXCollections.observableList(new ArrayList<>());

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

        needToSaveOnExit(recentFiles);
        needToSaveOnExit(windowSize);
        needToSaveOnExit(dividerPosition);
        needToSave(uiFontFamily);
        needToSave(uiFontSize);
        needToSave(textFontFamily);
        needToSave(textFontSize);
    }

    private void needToSave(Observable value) {
        value.addListener(o -> save());
    }

    private void needToSaveOnExit(Observable value) {
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

    @JsonProperty("uiFontFamily")
    public String getUIFontFamily() {
        return uiFontFamily.get();
    }

    public void setUIFontFamily(String uiFontProperty) {
        this.uiFontFamily.set(uiFontProperty);
    }

    public DoubleProperty uiFontSizeProperty() {
        return uiFontSize;
    }

    @JsonProperty("uiFontSize")
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

    public ObservableList<FilePath> getRecentFiles() {
        return recentFiles;
    }

    public void setRecentFiles(List<FilePath> paths) {
        getRecentFiles().setAll(paths);
    }

    public void addRecentFile(FilePath path) {
        ObservableList<FilePath> files = getRecentFiles();
        synchronized (files) {
            files.remove(path);
            if (files.size() == RECENT_FILES_LIMIT) {
                files.remove(0);
            }

            files.add(path);
        }
    }
}
