package org.glavo.viewer;

import kala.platform.Platform;
import org.glavo.viewer.util.Logging;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

public class Options {
    private static volatile Options options;

    public static synchronized void parse(String[] args) {
        if (options != null) {
            throw new IllegalStateException("command line arguments have been initialized");
        }

        String homeDir = null;
        List<String> files;

        int i = 0;
        loop:
        while (i < args.length) {
            String arg = args[i++];
            switch (arg) {
                case "--home":
                    homeDir = args[++i];
                    break;
                default:
                    break loop;
            }
        }

        if (i < args.length) {
            if (i == args.length - 1) {
                files = Collections.singletonList(args[i]);
            } else {
                files = Arrays.asList(Arrays.copyOfRange(args, i, args.length));
            }
        } else {
            files = Collections.emptyList();
        }

        Path home = null;
        if (homeDir != null) {
            try {
                home = Paths.get(homeDir);
            } catch (InvalidPathException e) {
                Logging.LOGGER.log(Level.WARNING, "home folder path is invalid", e);
                System.exit(1);
            }
        }

        if (home == null) {
            home = Platform.CURRENT_PLATFORM.getAppDataDirectory("Glavo").resolve("ClassViewer");
        }

        options = new Options(home, files);
        Logging.start(home);
    }

    public static synchronized Options getOptions() {
        return options;
    }

    private final Path home;
    private final List<String> files;

    public Options(Path home, List<String> files) {
        this.home = home;
        this.files = files;
    }

    public Path getHome() {
        return home;
    }

    public List<String> getFiles() {
        return files;
    }
}
