package org.glavo.viewer;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.glavo.viewer.util.WindowDimension;

public final class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        // region init config
        Config.load(Options.getOptions().getHome().resolve("config.json"));
        Config config = Config.getConfig();

        if (config.getWindowSize() == null) {
            Rectangle2D bounds = Screen.getPrimary().getBounds();

            double defaultWidth;
            double defaultHeight;

            if (bounds.getWidth() >= bounds.getHeight()) {
                defaultHeight = bounds.getHeight() / 2;
                defaultWidth = defaultHeight * 1.8;
            } else {
                defaultWidth = bounds.getWidth() / 2;
                defaultHeight = defaultWidth / 1.8;
            }

            config.setWindowSize(new WindowDimension(defaultWidth, defaultHeight));
        }
        // endregion




    }

    public static void main(String[] args) {
        Options.parse(args);
        Application.launch(Main.class);
    }
}
