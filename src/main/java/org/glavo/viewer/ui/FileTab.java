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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import org.glavo.viewer.annotation.NotFXThread;
import org.glavo.viewer.file.FileHandle;
import org.glavo.viewer.file.FileType;
import org.glavo.viewer.file.VirtualFile;
import org.glavo.viewer.resources.I18N;
import org.glavo.viewer.util.FXUtils;
import org.glavo.viewer.util.Schedulers;

import java.util.ArrayList;

public final class FileTab extends Tab {
    private final FileType type;
    private final VirtualFile file;

    private final ObjectProperty<Node> sideBar = new SimpleObjectProperty<>();
    private final StringProperty statusText = new SimpleStringProperty();
    private final ObservableList<Node> statusBarItems = FXCollections.observableArrayList();

    private volatile FileHandle fileHandle;

    public FileTab(VirtualFile file, FileType type) {
        this.type = type;
        this.file = file;

        this.setGraphic(new ImageView(type.getImage()));
        this.setText(file.getFileName());
        this.setTooltip(new Tooltip(file.toString()));
        this.setContextMenu(new TabMenu());
        this.setOnClosed(event -> Schedulers.io().execute(() -> {
            file.getContainer().lock();
            try {
                FileHandle fileHandle = this.fileHandle;
                if (fileHandle != null) {
                    fileHandle.close();
                }
            } finally {
                file.getContainer().unlock();
                FXUtils.runLater(() -> Viewer.openedFiles.remove(file));
            }
        }));
    }

    public FileType getType() {
        return type;
    }

    public VirtualFile getFile() {
        return file;
    }

    @NotFXThread
    public void setFileHandle(FileHandle fileHandle) {
        file.getContainer().lock();
        try {
            FileHandle oldFileHandle = this.fileHandle;
            if (oldFileHandle != null) {
                oldFileHandle.setOnForceClose(null);
            }

            this.fileHandle = fileHandle;
            if (fileHandle != null) {
                fileHandle.setOnForceClose(() -> FXUtils.runInFx(() -> FXUtils.closeTab(this)));
            }
        } finally {
            file.getContainer().unlock();
        }
    }

    public ObjectProperty<Node> sideBarProperty() {
        return sideBar;
    }

    public Node getSideBar() {
        return sideBar.get();
    }

    public void setSideBar(Node sideBar) {
        this.sideBar.set(sideBar);
    }

    public String getStatusText() {
        return statusText.get();
    }

    public void setStatusText(String text) {
        statusText.set(text);
    }

    public StringProperty statusTextProperty() {
        return statusText;
    }

    public ObservableList<Node> getStatusBarItems() {
        return statusBarItems;
    }

    public final class TabMenu extends ContextMenu {
        public TabMenu() {
            MenuItem close = new MenuItem(I18N.getString("filesTabPane.menu.close"));
            close.setOnAction(event -> getTabPane().getTabs().remove(FileTab.this));

            MenuItem closeOtherTabs = new MenuItem(I18N.getString("filesTabPane.menu.closeOtherTabs"));
            closeOtherTabs.setOnAction(event -> getTabPane().getTabs().removeIf(it -> it != FileTab.this));

            MenuItem closeAllTabs = new MenuItem(I18N.getString("filesTabPane.menu.closeAllTabs"));
            closeAllTabs.setOnAction(event -> getTabPane().getTabs().clear());

            MenuItem closeTabsToTheLeft = new MenuItem(I18N.getString("filesTabPane.menu.closeTabsToTheLeft"));
            closeTabsToTheLeft.setOnAction(event -> {
                TabPane tabPane = getTabPane();
                ObservableList<Tab> tabs = tabPane.getTabs();
                int idx = tabs.indexOf(FileTab.this);
                if (idx > 0) {
                    var tabsToBeClosed = new ArrayList<Tab>(idx);
                    for (int i = 0; i < idx; i++) {
                        tabsToBeClosed.add(tabs.get(i));
                    }
                    tabs.remove(0, idx);
                    tabsToBeClosed.forEach(FXUtils::closeTab);
                }

                getTabPane().getSelectionModel().select(FileTab.this);
            });

            MenuItem closeTabsToTheRight = new MenuItem(I18N.getString("filesTabPane.menu.closeTabsToTheRight"));
            closeTabsToTheRight.setOnAction(event -> {
                TabPane tabPane = getTabPane();
                ObservableList<Tab> tabs = tabPane.getTabs();
                int idx = tabs.indexOf(FileTab.this);
                if (idx >= 0 && idx < tabs.size() - 1) {
                    var tabsToBeClosed = new ArrayList<Tab>();
                    for (int i = idx + 1; i < tabs.size(); i++) {
                        tabsToBeClosed.add(tabs.get(i));
                    }
                    tabsToBeClosed.forEach(FXUtils::closeTab);
                }
                getTabPane().getSelectionModel().select(FileTab.this);
            });

            this.getItems().setAll(close, closeOtherTabs, closeAllTabs, closeTabsToTheLeft, closeTabsToTheRight);
        }
    }
}
