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
package org.glavo.viewer.ui;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.glavo.viewer.resources.I18N;
import org.glavo.viewer.util.FXUtils;
import org.glavo.viewer.util.StringUtils;

public final class ExceptionDialog extends Dialog<ButtonType> {
    public ExceptionDialog(final Throwable exception) {
        var dialogPane = getDialogPane();
        FXUtils.init(dialogPane);

        dialogPane.getButtonTypes().addAll(ButtonType.OK);

        this.setHeaderText(I18N.getString("exception.dialog.header"));
        this.setContentText(exception.getLocalizedMessage());

        Label label = new Label(I18N.getString("exception.dialog.stacktrace"));

        TextArea textArea = new TextArea(StringUtils.getStackTrace(exception));
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(textArea, Priority.ALWAYS);

        var root = new VBox(8);
        root.setMaxWidth(Double.MAX_VALUE);
        root.getChildren().addAll(label, textArea);

        dialogPane.setExpandableContent(root);
    }
}
