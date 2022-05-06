package org.glavo.viewer.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.glavo.viewer.Config;
import org.glavo.viewer.resources.Images;
import org.glavo.viewer.util.Stylesheet;
import org.glavo.viewer.util.WindowDimension;

public final class Viewer {
    private static final ObservableList<Viewer> viewers = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());

    private final Stage stage;

    public Viewer(Stage stage, boolean isPrimary) {
        this.stage = stage;

        Config config = Config.getConfig();

        ViewerPane root = new ViewerPane();
        Scene scene = new Scene(root);
        stage.setWidth(config.getWindowSize().getWidth());
        stage.setHeight(config.getWindowSize().getHeight());
        if (config.getWindowSize().isMaximized()) {
            stage.setMaximized(true);
        }

        scene.getStylesheets().setAll(Stylesheet.getStylesheets());

        stage.setScene(scene);
        stage.getIcons().setAll(Images.logo32, Images.logo16);

        stage.setTitle("ClassViewer");
        stage.show();

        viewers.add(this);
        stage.setOnCloseRequest(e -> {
            if (isPrimary) {
                config.setWindowSize(stage.isMaximized()
                        ? new WindowDimension(true, config.getWindowSize().getWidth(), config.getWindowSize().getHeight())
                        : new WindowDimension(false, stage.getWidth(), stage.getHeight()));
            }

            viewers.remove(this);
        });
    }

    public Stage getStage() {
        return stage;
    }

    public void show() {
        getStage().show();
    }
}
