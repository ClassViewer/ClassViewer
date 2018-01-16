package org.glavo.viewer.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.glavo.viewer.util.ImageUtils;

public final class Viewer extends Application {
    public static final String TITLE = "ClassViewer";

    public static final int DEFAULT_WIDTH = 800;
    public static final int DEFAULT_HEIGHT = 500;

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
}
