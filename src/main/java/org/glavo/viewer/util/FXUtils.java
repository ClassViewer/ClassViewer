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
package org.glavo.viewer.util;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import kala.function.CheckedRunnable;
import org.glavo.viewer.annotation.FXThread;
import org.glavo.viewer.resources.Images;

public final class FXUtils {

    public static void runLater(CheckedRunnable<?> action) {
        Platform.runLater(action);
    }

    public static void runInFx(CheckedRunnable<?> action) {
        if (Platform.isFxApplicationThread()) {
            action.run();
        } else {
            Platform.runLater(action);
        }
    }

    @FXThread
    public static void closeTab(Tab tab) {
        Event event = new Event(tab, tab, Tab.TAB_CLOSE_REQUEST_EVENT);
        Event.fireEvent(tab, event);

        if (event.isConsumed()) {
            return;
        }

        TabPane tabPane = tab.getTabPane();
        // only switch to another tab if the selected tab is the one we're closing
        int index = tabPane.getTabs().indexOf(tab);
        if (index != -1) {
            tabPane.getTabs().remove(index);
        }
        if (tab.getOnClosed() != null) {
            Event.fireEvent(tab, new Event(Tab.CLOSED_EVENT));
        }
    }

    @FXThread
    public static void setLoading(TreeItem<?> item) {
        var progressIndicator = new ProgressIndicator();
        progressIndicator.setPrefSize(16, 16);
        item.setGraphic(progressIndicator);
    }

    @FXThread
    public static void setFailed(TreeItem<?> item, Throwable exception) {
        item.setGraphic(new ImageView(Images.failed));
    }

    private FXUtils() {
    }
}
