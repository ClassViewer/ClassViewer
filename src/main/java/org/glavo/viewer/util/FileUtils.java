package org.glavo.viewer.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class FileUtils {
    public static final Path VIEWER_HOME = getViewerHome();

    private static Path getViewerHome() {
        final String home = System.getProperty("viewer.home");
        Path homePath = (home == null ? Paths.get(System.getProperty("user.home"), ".viewer") : Paths.get(home)).toAbsolutePath();
        try {
            Files.createDirectories(homePath);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return homePath;
    }
}
