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

import org.glavo.viewer.Metadata;
import org.glavo.viewer.file.types.FileType;
import org.glavo.viewer.util.logging.Log;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public final class RecentFiles {

    public static RecentFiles Instance = null;

    public static void init() {
        if (Instance == null) {
            synchronized (RecentFiles.class) {
                if (Instance == null) {
                    Instance = new RecentFiles();
                }
            }
        }
    }

    private final List<RecentFile> list = Collections.synchronizedList(new LinkedList<>());

    private RecentFiles() {
        load();
        Runtime.getRuntime()
                .addShutdownHook(new Thread(this::save));
    }

    public File getLastOpenFile(FileType ft) {
        for (RecentFile rf : list) {
            if (rf.type == ft && Objects.equals(rf.url.getProtocol(), "file")) {
                try {
                    return new File(rf.url.toURI());
                } catch (URISyntaxException e) {
                    Log.error(e);
                }
            }
        }

        return null;
    }

    public File getLastOpenFile() {
        for (RecentFile rf : list) {
            if (Objects.equals(rf.url.getProtocol(), "file")) {
                try {
                    return new File(rf.url.toURI());
                } catch (URISyntaxException e) {
                    Log.error(e);
                }
            }
        }

        return null;
    }

    public List<RecentFile> getAll() {
        return list;
    }

    public void add(FileType fileType, File file) throws MalformedURLException {
        add(fileType, file.toURI().toURL());
    }

    public void add(FileType fileType, URL fileUrl) {
        add(new RecentFile(fileType, fileUrl));
    }

    public void remove(URL url) {
        list.removeIf(f -> f != null && Objects.equals(f.url, url));
    }

    private void add(RecentFile rf) {
        list.remove(rf);
        list.add(0, rf);

        if (list.size() > 20) {
            list.remove(list.size() - 1);
        }
    }

    private void addLast(RecentFile rf) {
        list.remove(rf);
        list.add(list.size(), rf);

        if (list.size() > 20) {
            list.remove(0);
        }
    }


    private void save() {
        try {
            Files.createDirectories(Metadata.VIEWER_DIRECTORY);

            Path p = Metadata.VIEWER_DIRECTORY.resolve("recentfiles");
            Log.info("Save recent files to file: " + p);
            if (Files.notExists(p)) {
                Files.createFile(p);
            }
            try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(p))) {
                for (RecentFile recentFile : list) {
                    pw.println(recentFile);
                }
            }
        } catch (IOException e) {
            ViewerAlert.logAndShowExceptionAlert(e);
        }
    }

    private void load() {
        try {
            Path p = Metadata.VIEWER_DIRECTORY.resolve("recentfiles");

            if (Files.notExists(p)) {
                Log.info("Recent files not exists");
                return;
            }
            Log.info("Load recent files from file: " + p);

            try (BufferedReader reader = Files.newBufferedReader(p)) {
                reader.lines().forEach(line -> {
                    if (line.contains("#=>")) {
                        try {
                            this.addLast(RecentFile.parse(line));
                        } catch (MalformedURLException | IllegalArgumentException e) {
                            Log.error(e);
                        }
                    }
                });
            }

        } catch (Throwable e) {
            Log.error(e);
        }
    }
}
