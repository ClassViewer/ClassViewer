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

import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.Tab;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;
import kala.function.CheckedSupplier;
import org.glavo.viewer.file.types.FileType;
import org.glavo.viewer.util.ImageUtils;
import org.glavo.viewer.util.Schedulers;
import org.glavo.viewer.util.logging.Log;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class Viewer extends Control {
    public static final String TITLE = "ClassViewer";

    public static final int DEFAULT_WIDTH = 1200;
    public static final int DEFAULT_HEIGHT = 675;

    final Stage stage;
    final Scene scene;

    public Viewer(Stage stage, boolean isPrimary) {
        this.stage = stage;
        this.scene = new Scene(this, DEFAULT_WIDTH, DEFAULT_HEIGHT);

        enableDragAndDrop(scene);

        stage.setScene(scene);
        stage.setTitle(Viewer.TITLE);
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

    ViewerSkin getViewerSkin() {
        return (ViewerSkin) getSkin();
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new ViewerSkin(this);
    }

    public ViewerTabPane getTabPane() {
        return getViewerSkin().tabPane;
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
        if (url == null) {
            return;
        }

        CompletableFuture.supplyAsync(CheckedSupplier.of(() -> {
            if (type != null) {
                return type.open(this, url);
            } else {
                for (FileType t : FileType.fileTypes) {
                    if (t.accept(url)) {
                        ViewerTab ans = t.open(this, url);
                        RecentFiles.Instance.add(t, url);
                        return ans;
                    }
                }
            }
            return null;
        })).whenCompleteAsync((tab, exception) -> {
            if (exception == null) {
                addTab(tab);
                // TODO: getMenuBar().updateRecentFiles();
            } else {
                ViewerAlert.logAndShowExceptionAlert(exception);
            }
        }, Schedulers.javafx());
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
        CompletableFuture.supplyAsync(() -> {
            ArrayList<ViewerTab> ans = new ArrayList<>();

            tag:
            for (URL url : urls) {
                for (FileType type : FileType.fileTypes) {
                    try {
                        if (type.accept(url)) {
                            ans.add(type.open(this, url));
                            RecentFiles.Instance.add(type, url);
                            continue tag;
                        }
                    } catch (Exception ex) {
                        ViewerAlert.logAndShowExceptionAlert(ex);
                    }
                }
            }
            return ans;
        }).whenCompleteAsync((result, exception) -> {
            if (exception == null) {
                this.addTabs(result);
            } else {
                ViewerAlert.logAndShowExceptionAlert(exception);
            }
        }, Schedulers.javafx());
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
