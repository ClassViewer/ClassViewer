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
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Tab;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.glavo.viewer.file.types.FileType;
import org.glavo.viewer.resources.I18N;
import org.glavo.viewer.util.ImageUtils;
import org.glavo.viewer.util.logging.Log;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public final class Viewer extends BorderPane {
    public static final String TITLE = "ClassViewer";

    public static final int DEFAULT_WIDTH = 1200;
    public static final int DEFAULT_HEIGHT = 675;

    private final Stage stage;
    private final Scene scene;

    private final ViewerMenuBar menuBar;
    private final ToolBar toolBar;
    private final Pane defaultText;
    private final ViewerTabPane tabPane;

    public Viewer(Stage stage, boolean isPrimary) {
        this.stage = stage;
        this.scene = new Scene(this, DEFAULT_WIDTH, DEFAULT_HEIGHT);

        Stylesheet.setStylesheet(scene);

        enableDragAndDrop(scene);

        stage.setScene(scene);
        stage.setTitle(TITLE);
        stage.getIcons().add(ImageUtils.loadImage("/icons/spy16.png"));
        stage.getIcons().add(ImageUtils.loadImage("/icons/spy32.png"));
        stage.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN).match(event)) {
                ViewerTab tab = (ViewerTab) getTabPane().getSelectionModel().getSelectedItem();
                if (tab != null) {
                    tab.showSearchBar();
                }
                event.consume();
            }
        });

        this.menuBar = new ViewerMenuBar(this);
        this.toolBar = new ToolBar();
        {
            Button openFile = new Button(null, new ImageView(ImageUtils.openFileImage));
            openFile.setOnAction(event -> openFile());
            Tooltip openFileTip = new Tooltip(I18N.getString("openFileButton.tooltip"));
            openFile.setTooltip(openFileTip);

            Button openFolder = new Button(null, new ImageView(ImageUtils.openFolderImage));
            openFolder.setOnAction(event -> openFolder());
            Tooltip openFolderTip = new Tooltip(I18N.getString("openFolderButton.tooltip"));
            openFolder.setTooltip(openFolderTip);

            toolBar.getItems().addAll(openFile, openFolder);
        }

        this.tabPane = new ViewerTabPane(this);
        this.defaultText = createDefaultText();

        this.setTop(new VBox(menuBar, toolBar));
        this.centerProperty().bind(Bindings.createObjectBinding(
                () -> tabPane.getTabs().isEmpty() ? defaultText : tabPane,
                tabPane.getTabs()));

        stage.setOnShown(event -> Log.info("Show " + this));
        stage.setOnCloseRequest(event -> Log.info("Close " + this));
    }

    private void enableDragAndDrop(Scene scene) {
        scene.setOnDragOver(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            } else {
                event.consume();
            }
        });
        // Dropping over surface
        scene.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                success = true;
                for (File file : db.getFiles()) {
                    try {
                        openFile(file.toURI().toURL());
                    } catch (MalformedURLException e) {
                        ViewerAlert.logAndShowExceptionAlert(e);
                    }
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    private Pane createDefaultText() {
        Text openFileText = new Text(I18N.getString("defaultText.openFile"));
        openFileText.setFill(Color.GRAY);
        Hyperlink openFileLink = new Hyperlink(menuBar.fileMenu.openFileItem.getAccelerator().getDisplayText());
        openFileLink.setOnAction(event -> openFile());

        Text openFolderText = new Text(I18N.getString("defaultText.openFolder"));
        openFolderText.setFill(Color.GRAY);
        Hyperlink openFolderLink = new Hyperlink(menuBar.fileMenu.openFolderItem.getAccelerator().getDisplayText());
        openFolderLink.setOnAction(event -> openFolder());

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

    public void openFile() {
        File file = ViewerFileChooser.showFileChooser(stage);
        if (file != null) {
            try {
                openFile(file.toURI().toURL());
            } catch (MalformedURLException e) {
                ViewerAlert.logAndShowExceptionAlert(e);
            }
        }
    }

    public void openFolder() {
        File file = ViewerFileChooser.showDirectoryChooser(stage);
        if (file != null) {
            try {
                openFile(file.toURI().toURL());
            } catch (MalformedURLException e) {
                ViewerAlert.logAndShowExceptionAlert(e);
            }
        }
    }

    public void openFile(URL url) {
        openFile(null, url);
    }

    public void openFile(FileType type, URL url) {
        try {
            Log.info("Open file: " + url);
            if (url != null) {
                OpenFileTask task = new OpenFileTask(this, type, url);
                task.setOnSucceeded((ViewerTab tab) -> {
                    addTab(tab);
                    getMenuBar().updateRecentFiles();
                });
                task.startInNewThread();
            }
        } catch (Exception e) {
            ViewerAlert.logAndShowExceptionAlert(e);
        }
    }

    public void addTab(ViewerTab tab) {
        if (tab != null) {
            getTabPane().getTabs().add(getTabPane().getSelectionModel().getSelectedIndex() + 1, tab);
            getTabPane().getSelectionModel().select(tab);
        }
    }

    public void openFiles(List<File> files) {
        ArrayList<URL> urls = new ArrayList<>(files.size());
        for (File file : files) {
            if (file != null) {
                try {
                    urls.add(file.toURI().toURL());
                } catch (MalformedURLException e) {
                    ViewerAlert.logAndShowExceptionAlert(e);
                }
            }
        }
        openUrls(urls);
    }

    public void openUrls(List<URL> urls) {
        OpenFilesTask task = new OpenFilesTask(this, urls);
        task.setOnSucceeded(this::addTabs);
    }

    public void addTabs(List<ViewerTab> tabs) {
        if (tabs == null || tabs.isEmpty()) {
            return;
        }

        if (tabs.size() == 1) {
            Tab tab = tabs.getFirst();
            getTabPane().getTabs().add(getTabPane().getSelectionModel().getSelectedIndex() + 1, tab);
            getTabPane().getSelectionModel().select(tab);
            return;
        }

        getTabPane().getTabs().addAll(getTabPane().getSelectionModel().getSelectedIndex() + 1, tabs);
    }

    public void removeTab(ViewerTab tab) {
        getTabPane().getTabs().remove(tab);
    }

    public Stage getStage() {
        return stage;
    }
}
