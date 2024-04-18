package org.glavo.viewer;

import javafx.application.Application;
import org.glavo.viewer.util.logging.Logger;

public class Launcher {
    public static void main(String[] args) throws Throwable {
        Options.parse(args);
        Logger.LOGGER.start(Options.getOptions().getHome().resolve("logs"));
        Config.load();
        Application.launch(Main.class);
    }
}
