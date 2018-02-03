package org.glavo.viewer.gui;

import org.glavo.viewer.gui.filetypes.FileType;
import org.glavo.viewer.util.Log;

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

    public static final RecentFiles Instance = new RecentFiles();

    public static void init() {

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

    private void save() {
        try {
            Path p = Options.path.resolve("recentfiles");
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
            Log.error(e);
            ViewerAlert.showExceptionAlert(e);
        }
    }

    private void load() {
        try {
            Path p = Options.path.resolve("recentfiles");

            if (Files.notExists(p)) {
                Log.warning("Recent files not exists");
                return;
            }
            Log.info("Load recent files from file: " + p);

            try (BufferedReader reader = Files.newBufferedReader(p)) {
                reader.lines().forEach(line -> {
                    if (line.contains("#=>")) {
                        try {
                            list.add(RecentFile.parse(line));
                        } catch (MalformedURLException | IllegalArgumentException e) {
                            Log.error(e);
                        }
                    }
                });
            }

        } catch (IOException e) {
            Log.error(e);
            ViewerAlert.showExceptionAlert(e);
        }
    }
}
