package org.glavo.viewer;

import org.glavo.viewer.util.JavaFXPatcher;

import javax.swing.*;

public final class Launcher {
    private static void checkJFX() {
        try {
            Class.forName("javafx.scene.control.Menu");
        } catch (ClassNotFoundException e) {
            JavaFXPatcher.tryPatch();
        }
    }

    public static void main(String[] args) {
        initLookAndFeel();
        checkJFX();

        Main.main(args);
    }

    private static void initLookAndFeel() {
        if (System.getProperty("swing.defaultlaf") == null) {
            try {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName());
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
}
