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
import javafx.scene.control.*;
import javafx.stage.Stage;
import kala.function.CheckedRunnable;
import org.glavo.viewer.annotation.FXThread;
import org.glavo.viewer.resources.Images;
import org.glavo.viewer.ui.ExceptionDialog;

public final class FXUtils {

    public static void setIcons(Stage stage) {
        stage.getIcons().setAll(Images.logo32, Images.logo16);
    }

    public static void init(DialogPane dialogPane) {
        setIcons(((Stage) dialogPane.getScene().getWindow()));
        Stylesheet.setStylesheet(dialogPane.getStylesheets());
    }

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
    public static void showExceptionDialog(String title, Throwable exception) {
        var exceptionDialog = new ExceptionDialog(exception);
        exceptionDialog.setTitle(title);
        exceptionDialog.show();
    }

    @FXThread
    public static Hyperlink exceptionDialogLink(String message, Throwable exception) {
        Hyperlink hyperlink = new Hyperlink(message);
        hyperlink.setOnAction(event -> showExceptionDialog(message, exception));
        return hyperlink;
    }

    private FXUtils() {
    }
}
