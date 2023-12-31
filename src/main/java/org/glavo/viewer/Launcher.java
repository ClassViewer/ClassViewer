package org.glavo.viewer;

import javafx.application.Application;

public class Launcher {
    public static void main(String[] args) throws Throwable {
        Options.parse(args);
        Config.load();
        Application.launch(Main.class);
    }
}
