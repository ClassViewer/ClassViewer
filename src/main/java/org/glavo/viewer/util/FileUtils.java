package org.glavo.viewer.util;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class FileUtils {
    public static final long SMALL_FILE_LIMIT = 10 * 1024 * 1024; // 10 MiB


    public static final Path VIEWER_HOME = getViewerHome();

    public static final ExecutorService ioThread = Executors.newSingleThreadExecutor();

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

    public static final String TMP_EXT = ".tmp";

    public static Path tmpFile(Path path) {
        return path.resolveSibling(path.getFileName().toString() + TMP_EXT);
    }

    public static void save(Path path, String value) throws IOException {
        path = path.toAbsolutePath();
        Path tmpPath = tmpFile(path);
        Files.writeString(tmpPath, value);
        Files.move(tmpPath, path, StandardCopyOption.REPLACE_EXISTING);
    }

    public static void save(Path path, Action<Writer, IOException> action) throws IOException {
        path = path.toAbsolutePath();
        Path tmpPath = tmpFile(path);

        try (Writer writer = Files.newBufferedWriter(tmpPath)) {
            action.accept(writer);
        }

        Files.move(tmpPath, path, StandardCopyOption.REPLACE_EXISTING);
    }

    @FunctionalInterface
    public interface Action<T, Ex extends Throwable> {
        void accept(T value) throws Ex;
    }
}
