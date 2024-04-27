package org.glavo.viewer;

import javafx.application.Application;

import java.nio.file.Path;

import static org.glavo.viewer.util.logging.Logger.LOGGER;

public class Launcher {
    public static void main(String[] args) throws Throwable {
        Options options = Options.load(args);
        Path home = options.home();
        LOGGER.start(home.resolve("logs"));
        Settings.load(home);
        Config.load(home);
        Application.launch(Main.class);
    }
}
