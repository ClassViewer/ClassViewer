/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 Glavo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.glavo.viewer;

import javafx.application.Application;
import javafx.stage.Stage;
import org.glavo.viewer.ui.RecentFile;
import org.glavo.viewer.ui.Viewer;
import org.glavo.viewer.util.PropertiesUtils;
import org.glavo.viewer.util.logging.Log;
import org.glavo.viewer.util.CrashHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class Main extends Application {

    private static Main INSTANCE;

    public static Main getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("Main instance has not been created");
        }

        return INSTANCE;
    }

    @Override
    public void init() throws Exception {
        INSTANCE = this;
    }

    @Override
    public void start(Stage primaryStage) {
        var viewer = new Viewer(primaryStage, true);

        if (this.getParameters() != null && this.getParameters().getUnnamed() != null) {
            List<String> args = this.getParameters().getUnnamed();
            ArrayList<File> files = new ArrayList<>(args.size());
            for (String arg : args) {
                files.add(new File(arg));
            }
            javafx.application.Platform.runLater(() -> viewer.openFiles(files));
        }

        viewer.getStage().show();
    }

    @Override
    public void stop() {
        RecentFile.saveRecentFiles();
        Log.shutdown();
        INSTANCE = null;
    }

    public static void main(String[] args) {
        Log.start(Metadata.VIEWER_DIRECTORY.resolve("logs"));
        Thread.setDefaultUncaughtExceptionHandler(CrashHandler.INSTANCE);
        PropertiesUtils.loadProperties(Metadata.VIEWER_DIRECTORY.resolve("viewer.properties"));

        Log.info("launch application");
        Application.launch(Main.class, args);
    }
}
