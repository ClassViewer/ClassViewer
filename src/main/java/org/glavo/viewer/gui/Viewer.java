package org.glavo.viewer.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.glavo.viewer.util.ImageUtils;
import org.glavo.viewer.util.Log;

import java.io.File;
import java.util.List;

public final class Viewer extends Application {
    public static final String TITLE = "ClassViewer";

    public static final int DEFAULT_WIDTH = 1000;
    public static final int DEFAULT_HEIGHT = 600;

    public static void main(String[] args) {
        Options.init();
        Application.launch(Viewer.class, args);
    }

    private Stage stage;
    private Scene scene;
    private BorderPane pane;

    private ViewerMenuBar menuBar;
    private ViewerTabPane tabPane;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        this.pane = new BorderPane();
        this.menuBar = new ViewerMenuBar(this);
        this.tabPane = new ViewerTabPane(this);

        pane.setTop(menuBar);
        pane.setCenter(tabPane);

        this.scene = new Scene(pane, DEFAULT_WIDTH, DEFAULT_HEIGHT);

        stage.setScene(scene);
        stage.setTitle(TITLE);
        stage.getIcons().add(ImageUtils.loadImage("/icons/spy16.png"));
        stage.getIcons().add(ImageUtils.loadImage("/icons/spy32.png"));
        stage.show();
    }

    @SuppressWarnings("unchecked")
    public void openFile() {
        try {
            File file = ViewerFileChooser.showFileChooser(stage);
            Log.info("Open file: " + file);
            if (file != null) {
                OpenFileTask task = new OpenFileTask(this, file.toURI().toURL());
                task.setOnSucceeded(event -> {
                    List<Tab> tabs = (List<Tab>) event.getSource().getValue();
                    if (tabs != null && !tabs.isEmpty()) {
                        if (tabs.size() == 1) {
                            tabPane.getTabs().add(tabs.get(0));
                            tabPane.getSelectionModel().select(tabs.get(0));
                        } else {
                            tabPane.getTabs().addAll(tabs);
                        }
                    }
                });
                task.runInNewThread();
            }
        } catch (Exception e) {
            Log.error(e);
            ViewerAlert.exceptionAlert(e);
        }
    }

    public Stage getStage() {
        return stage;
    }

    public Scene getScene() {
        return scene;
    }

    public BorderPane getPane() {
        return pane;
    }

    public ViewerMenuBar getMenuBar() {
        return menuBar;
    }

    public ViewerTabPane getTabPane() {
        return tabPane;
    }
}
