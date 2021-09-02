package org.glavo.viewer.util;

import javax.swing.*;
import java.util.ResourceBundle;

public final class JavaFXPatcher {
    private static final ResourceBundle resources = ResourceBundle.getBundle("viewer.patcher", UTF8Control.Control);

    public static void tryPatch() {
        JOptionPane.showMessageDialog(
                null,
                resources.getString("viewer.javafx.missing.text"),
                resources.getString("viewer.javafx.missing.title"),
                JOptionPane.ERROR_MESSAGE
        );
        System.err.println("Patch JavaFX Failed");
        System.exit(1);
    }
}
