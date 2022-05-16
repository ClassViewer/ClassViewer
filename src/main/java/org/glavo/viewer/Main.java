package org.glavo.viewer;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.glavo.viewer.file.types.TextFileType;
import org.glavo.viewer.ui.Viewer;
import org.glavo.viewer.util.FileUtils;
import org.glavo.viewer.util.TaskUtils;
import org.glavo.viewer.util.WindowDimension;

import java.util.HashSet;
import java.util.Set;

public final class Main extends Application {

    private static Main app;

    public static Main getApplication() {
        return app;
    }

    @Override
    public void init() throws Exception {
        app = this;
    }

    @Override
    public void start(Stage stage) throws Exception {

        // Part of the configuration needs to be initialized during the start process
        // region init config
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

        Viewer viewer = new Viewer(stage, true);

        viewer.show();
    }

    @Override
    public void stop() throws Exception {
        if (Config.getConfig().isNeedToSaveOnExit()) {
            Config.getConfig().save();
        }

        FileUtils.ioThread.shutdown();
        TaskUtils.taskPool.shutdown();
        TextFileType.highlightPool.shutdown();

        app = null;
    }
}
