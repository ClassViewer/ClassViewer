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
package org.glavo.viewer.ui;

import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import org.glavo.viewer.util.ImageUtils;

import static org.glavo.viewer.ui.Viewer.resource;

public final class ViewerMainPane extends BorderPane {
    private final Viewer viewer;

    private final ViewerMenuBar menuBar;
    private final ToolBar toolBar;
    private final Pane defaultText;
    private final ViewerTabPane tabPane;

    public ViewerMainPane(Viewer viewer) {
        this.viewer = viewer;

        this.menuBar = new ViewerMenuBar(viewer);
        this.toolBar = new ToolBar();
        {
            Button openFile = new Button(null, new ImageView(ImageUtils.openFileImage));
            openFile.setOnAction(event -> viewer.openFile());
            Tooltip openFileTip = new Tooltip(resource.getString("openFileButton.tooltip"));
            openFile.setTooltip(openFileTip);

            Button openFolder = new Button(null, new ImageView(ImageUtils.openFolderImage));
            openFolder.setOnAction(event -> viewer.openFolder());
            Tooltip openFolderTip = new Tooltip(resource.getString("openFolderButton.tooltip"));
            openFolder.setTooltip(openFolderTip);

            toolBar.getItems().addAll(openFile, openFolder);
        }

        this.tabPane = new ViewerTabPane(viewer);
        this.defaultText = createDefaultText();

        this.setTop(new VBox(menuBar, toolBar));
        this.centerProperty().bind(Bindings.createObjectBinding(
                () -> tabPane.getTabs().isEmpty() ? defaultText : tabPane,
                tabPane.getTabs()));
    }

    private Pane createDefaultText() {
        Text openFileText = new Text(Viewer.resource.getString("defaultText.openFile"));
        openFileText.setFill(Color.GRAY);
        Hyperlink openFileLink = new Hyperlink(menuBar.fileMenu.openFileItem.getAccelerator().getDisplayText());
        openFileLink.setOnAction(event -> viewer.openFile());

        Text openFolderText = new Text(Viewer.resource.getString("defaultText.openFolder"));
        openFolderText.setFill(Color.GRAY);
        Hyperlink openFolderLink = new Hyperlink(menuBar.fileMenu.openFolderItem.getAccelerator().getDisplayText());
        openFolderLink.setOnAction(event -> viewer.openFolder());

        TextFlow text = new TextFlow(
                openFileText, new Text(" "), openFileLink, new Text("\n"),
                openFolderText, new Text(" "), openFolderLink
        );
        text.setTextAlignment(TextAlignment.LEFT);

        FlowPane pane = new FlowPane(text);
        pane.setAlignment(Pos.CENTER);
        return pane;
    }

    public ViewerMenuBar getMenuBar() {
        return menuBar;
    }

    public ViewerTabPane getTabPane() {
        return tabPane;
    }
}
