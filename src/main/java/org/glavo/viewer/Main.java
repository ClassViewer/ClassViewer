package org.glavo.viewer;

import javafx.application.Application;
import org.glavo.viewer.gui.Options;
import org.glavo.viewer.gui.Viewer;
import org.glavo.viewer.util.Log;

public final class Main {
    public static void main(String[] args) {
        Options.init();
        Log.info("launch application");
        Application.launch(Viewer.class, args);
    }
}
