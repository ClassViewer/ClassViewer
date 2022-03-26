package org.glavo.viewer;

import org.glavo.viewer.util.JavaFXPatcher;

public final class Launcher {
    public static void main(String[] args) throws Throwable {
        checkJFX();
        Main.main(args);
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
