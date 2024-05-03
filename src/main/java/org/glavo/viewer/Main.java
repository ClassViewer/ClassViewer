/*
 * Copyright 2024 Glavo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.glavo.viewer;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.glavo.viewer.util.Schedulers;
import org.glavo.viewer.ui.Viewer;
import org.glavo.viewer.util.Stylesheet;
import org.glavo.viewer.util.WindowDimension;

import static org.glavo.viewer.util.logging.Logger.LOGGER;

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

        Stylesheet.init();

        Viewer viewer = new Viewer(stage, true);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        if (Config.getConfig().isNeedToSaveOnExit()) {
            Config.getConfig().save();
        }

        Schedulers.shutdown();
        LOGGER.shutdown();
        app = null;
    }
}
