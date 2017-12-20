package org.glavo.viewer.gui.support;

import org.glavo.viewer.util.Log;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

/**
 * Recent open file list.
 */
public class RecentFiles {

    public static final RecentFiles INSTANCE = new RecentFiles();


    private final LinkedList<RecentFile> list = new LinkedList<>();
    private boolean listChanged = false;

    private RecentFiles() {
        load();
        Runtime.getRuntime()
                .addShutdownHook(new Thread(this::save));
    }

    public File getLastOpenFile(FileType ft) {
        for (RecentFile rf : list) {
            if (rf.type == ft && rf.url.toString().startsWith("file")) {
                try {
                    return new File(rf.url.toURI());
                } catch (URISyntaxException e) {
                    e.printStackTrace(System.err);
                }
            }
        }

        return null;
    }

    public File getLastOpenFile() {
        for (RecentFile rf : list) {
            if (rf.url.toString().startsWith("file")) {
                try {
                    return new File(rf.url.toURI());
                } catch (URISyntaxException e) {
                    e.printStackTrace(System.err);
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
        Log.warning("RecentFiles: " + fileType + ", " + fileUrl);
        add(new RecentFile(fileType, fileUrl));
    }

    private void add(RecentFile rf) {
        listChanged = true;
        list.remove(rf);
        list.addFirst(rf);
        // todo
        if (list.size() > 20) {
            list.removeLast();
        }
    }

    private void save() {
        Preferences preferences = Preferences.userRoot().node("ClassViewer");
        Log.log("saving recent files...");
        preferences.put("recentfiles", list.stream()
                .map(RecentFile::toString)
                .collect(Collectors.joining("\n")));
        try {
            preferences.flush();
        } catch (BackingStoreException e) {
            e.printStackTrace();
        }
    }

    private void load() {
        Preferences preferences = Preferences.userRoot().node("ClassViewer");
        String data = preferences.get("recentfiles", null);
        if (data != null) {
            Log.log("loading recent files..");
            for (String line : data.split("\n")) {
                if (line.contains("#=>")) {
                    try {
                        list.addLast(new RecentFile(line));
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
