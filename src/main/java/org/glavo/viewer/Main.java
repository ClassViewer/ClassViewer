package org.glavo.viewer;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
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

        Config config = Config.getConfig();
        Set<String> fonts = null;
        if (config.getUIFontFamily() == null) {
            fonts = new HashSet<>(Font.getFamilies());

            String[] defaultUIFontFamilies = {
                    "PingFang SC",
                    "Microsoft YaHei UI",
                    "Ubuntu",
                    "Segoe UI"
            };

            for (String font : defaultUIFontFamilies) {
                if (fonts.contains(font)) {
                    config.setUIFontFamily(font);
                    break;
                }
            }
        }

        if (config.getUIFontSize() <= 0) {
            config.setUIFontSize(14);
        }

        if (config.getTextFontFamily() == null) {
            if (fonts == null) {
                fonts = new HashSet<>(Font.getFamilies());
            }

            String[] defaultTextFontFamilies = {
                    "Consolas",
                    "Source Code Pro",
                    "Fira Code",
                    "DejaVu Sans Mono"
            };

            for (String font : defaultTextFontFamilies) {
                if (fonts.contains(font)) {
                    config.setTextFontFamily(font);
                    break;
                }
            }
        }

        if (config.getTextFontSize() <= 0) {
            config.setTextFontSize(16);
        }
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

        app = null;
    }
}
