package org.glavo.viewer;

import javafx.application.Application;

import static org.glavo.viewer.util.logging.Logger.LOGGER;

public class Launcher {
    public static void main(String[] args) throws Throwable {
        Options.parse(args);
        LOGGER.start(Options.getOptions().getHome().resolve("logs"));
        Config.load();
        Application.launch(Main.class);
    }
}
