/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 Glavo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.glavo.viewer.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.glavo.viewer.Metadata;
import org.glavo.viewer.annotation.FXThread;
import org.glavo.viewer.file.types.FileType;
import org.glavo.viewer.util.logging.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record RecentFile(FileType type, URL url) {

    private static final int MAX_RECENT_FILES = 20;
    private static final ObservableList<RecentFile> RECENT_FILES = FXCollections.observableList(loadRecentFiles());

    private static LinkedList<RecentFile> loadRecentFiles() {
        Path p = Metadata.VIEWER_DIRECTORY.resolve("recentfiles");

        if (Files.exists(p)) {
            try {
                Log.info("Load recent files from file: " + p);

                LinkedList<RecentFile> files;
                try (BufferedReader reader = Files.newBufferedReader(p)) {
                    files = reader.lines()
                            .filter(it -> it.contains("#=>"))
                            .flatMap(it -> {
                                try {
                                    return Stream.of(RecentFile.parse(it));
                                } catch (MalformedURLException | IllegalArgumentException e) {
                                    Log.warning("Failed to parse recent file: " + it, e);
                                    return Stream.empty();
                                }
                            })
                            .collect(Collectors.toCollection(LinkedList::new));
                }

                if (files.size() > MAX_RECENT_FILES) {
                    int n = files.size() - MAX_RECENT_FILES;
                    for (int i = 0; i < n; i++) {
                        files.removeFirst();
                    }
                }

                return files;
            } catch (Throwable e) {
                Log.warning("Failed to load recent files", e);
            }
        } else {
            Log.info("Recent files not exists");
        }

        return new LinkedList<>();
    }

    public static ObservableList<RecentFile> getRecentFiles() {
        return RECENT_FILES;
    }

    public static void saveRecentFiles() {
        try {
            Files.createDirectories(Metadata.VIEWER_DIRECTORY);

            Path p = Metadata.VIEWER_DIRECTORY.resolve("recentfiles");
            Log.info("Save recent files to " + p);

            try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(p, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING))) {
                for (RecentFile recentFile : RECENT_FILES) {
                    pw.println(recentFile);
                }
            }
        } catch (IOException e) {
            Log.warning("Failed to save recent files", e);
        }
    }

    @FXThread
    public static void addRecentFile(RecentFile recentFile) {
        RECENT_FILES.remove(recentFile);

        if (RECENT_FILES.size() == MAX_RECENT_FILES) {
            RECENT_FILES.removeLast();
        }

        RECENT_FILES.addFirst(recentFile);
    }

    @FXThread
    public static void addRecentFile(FileType fileType, File file) throws MalformedURLException {
        addRecentFile(fileType, file.toURI().toURL());
    }

    @FXThread
    public static void addRecentFile(FileType fileType, URL fileUrl) {
        addRecentFile(new RecentFile(fileType, fileUrl));
    }

    @FXThread
    public static File getLastFile() {
        for (RecentFile rf : RECENT_FILES) {
            if (Objects.equals(rf.url().getProtocol(), "file")) {
                try {
                    return new File(rf.url().toURI());
                } catch (URISyntaxException e) {
                    Log.warning("Failed to convert URL " + rf.url + "to file", e);
                }
            }
        }

        return null;
    }

    public static RecentFile parse(String s) throws MalformedURLException {
        Objects.requireNonNull(s);
        if (!s.contains("#=>")) {
            return null;
        }
        return new RecentFile(FileType.valueOf(s.split("#=>")[0]),
                URI.create(s.split("#=>")[1]).toURL());
    }

    @Override
    public String toString() {
        return type + "#=>" + url;
    }
}
