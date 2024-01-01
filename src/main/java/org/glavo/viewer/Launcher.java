package org.glavo.viewer;

import javafx.application.Application;
import org.glavo.viewer.util.Logging;

public class Launcher {
    public static void main(String[] args) throws Throwable {
        Options.parse(args);
        Logging.start(Options.getOptions().getHome());
        Config.load();
        Application.launch(Main.class);
    }
}
