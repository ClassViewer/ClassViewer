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
import javafx.scene.control.SkinBase;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import org.glavo.viewer.resources.I18N;
import org.glavo.viewer.util.ImageUtils;

public final class ViewerSkin extends SkinBase<Viewer> {
    private final BorderPane rootPane;

    final ViewerMenuBar menuBar;
    final ToolBar toolBar;
    final Pane defaultText;
    final ViewerTabPane tabPane;

    ViewerSkin(Viewer viewer) {
        super(viewer);
        Stylesheet.setStylesheet(viewer.scene);

        this.menuBar = new ViewerMenuBar(viewer);
        this.toolBar = new ToolBar();
        {
            Button openFile = new Button(null, new ImageView(ImageUtils.openFileImage));
            openFile.setOnAction(event -> viewer.openFile());
            Tooltip openFileTip = new Tooltip(I18N.getString("openFileButton.tooltip"));
            openFile.setTooltip(openFileTip);

            Button openFolder = new Button(null, new ImageView(ImageUtils.openFolderImage));
            openFolder.setOnAction(event -> viewer.openFolder());
            Tooltip openFolderTip = new Tooltip(I18N.getString("openFolderButton.tooltip"));
            openFolder.setTooltip(openFolderTip);

            toolBar.getItems().addAll(openFile, openFolder);
        }

        this.tabPane = new ViewerTabPane(viewer);
        this.defaultText = createDefaultText();

        this.rootPane = new BorderPane();

        this.rootPane.setTop(new VBox(menuBar, toolBar));
        this.rootPane.centerProperty().bind(Bindings.createObjectBinding(
                () -> tabPane.getTabs().isEmpty() ? defaultText : tabPane,
                tabPane.getTabs()));

        this.getChildren().add(rootPane);
    }

    private Pane createDefaultText() {
        var pane = new GridPane();
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(8);

        Text openFileText = new Text(I18N.getString("defaultText.openFile"));
        openFileText.setFill(Color.GRAY);
        Hyperlink openFileLink = new Hyperlink("Ctrl+O");
        openFileLink.setOnAction(event -> getSkinnable().openFile());

        Text openFolderText = new Text(I18N.getString("defaultText.openFolder"));
        openFolderText.setFill(Color.GRAY);
        Hyperlink openFolderLink = new Hyperlink("Ctrl+Shift+O");
        openFolderLink.setOnAction(event -> getSkinnable().openFolder());

        pane.add(openFileText, 0, 0);
        pane.add(openFileLink, 1, 0);
        pane.add(openFolderText, 0, 1);
        pane.add(openFolderLink, 1, 1);
        return pane;
    }
}
