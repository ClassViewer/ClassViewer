package org.glavo.viewer;

import javafx.application.Application;
import org.glavo.viewer.util.JavaFXPatcher;

public class Launcher {
    public static void main(String[] args) throws Throwable {
        Options.parse(args);
        checkJFX();
        Config.load();
        Application.launch(Main.class);
    }

    private static void checkJFX() throws Throwable {
        try {
            Class.forName("javafx.application.Application");
            return;
        } catch (ClassNotFoundException ignored) {
        }
        JavaFXPatcher.tryPatch();
    }
}
