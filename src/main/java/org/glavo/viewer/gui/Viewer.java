package org.glavo.viewer.gui;

import javafx.application.Application;
import javafx.stage.Stage;
import org.glavo.viewer.util.Log;

import java.util.Map;

public final class Viewer extends Application {
    public static void main(String[] args) {
        Options.init();
        Application.launch(Viewer.class, args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {

    }
}
