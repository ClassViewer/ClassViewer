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
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import org.glavo.viewer.file2.FileType;
import org.glavo.viewer.file2.VirtualFile;
import org.glavo.viewer.resources.I18N;

public final class FileTab2 extends Tab {
    private final FileType type;
    private final VirtualFile file;

    private final ObjectProperty<Node> sideBar = new SimpleObjectProperty<>();
    private final ObjectProperty<Node> statusBar = new SimpleObjectProperty<>();

    public FileTab2(FileType type, VirtualFile file) {
        this.type = type;
        this.file = file;

        this.setGraphic(new ImageView(type.getImage()));
        this.setText(file.getFileName());
        this.setTooltip(new Tooltip(file.toString()));
        this.setContextMenu(new TabMenu());
    }

    public FileType getType() {
        return type;
    }

    public VirtualFile getFile() {
        return file;
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

    public ObjectProperty<Node> statusBarProperty() {
        return statusBar;
    }

    public Node getStatusBar() {
        return statusBar.get();
    }

    public void setStatusBar(Node statusBar) {
        this.statusBar.set(statusBar);
    }

    public final class TabMenu extends ContextMenu {
        public TabMenu() {
            MenuItem close = new MenuItem(I18N.getString("filesTabPane.menu.close"));
            close.setOnAction(event -> getTabPane().getTabs().remove(FileTab2.this));

            MenuItem closeOtherTabs = new MenuItem(I18N.getString("filesTabPane.menu.closeOtherTabs"));
            closeOtherTabs.setOnAction(event -> getTabPane().getTabs().removeIf(it -> it != FileTab2.this));

            MenuItem closeAllTabs = new MenuItem(I18N.getString("filesTabPane.menu.closeAllTabs"));
            closeAllTabs.setOnAction(event -> getTabPane().getTabs().clear());

            MenuItem closeTabsToTheLeft = new MenuItem(I18N.getString("filesTabPane.menu.closeTabsToTheLeft"));
            closeTabsToTheLeft.setOnAction(event -> {
                TabPane tabPane = getTabPane();
                int idx = tabPane.getTabs().indexOf(FileTab2.this);
                if (idx > 0) {
                    tabPane.getTabs().remove(0, idx);
                }

                getTabPane().getSelectionModel().select(FileTab2.this);
            });

            MenuItem closeTabsToTheRight = new MenuItem(I18N.getString("filesTabPane.menu.closeTabsToTheRight"));
            closeTabsToTheRight.setOnAction(event -> {
                TabPane tabPane = getTabPane();
                int idx = tabPane.getTabs().indexOf(FileTab2.this);
                if (idx >= 0 && idx < tabPane.getTabs().size() - 1) {
                    tabPane.getTabs().remove(idx + 1, tabPane.getTabs().size());
                }
                getTabPane().getSelectionModel().select(FileTab2.this);
            });

            this.getItems().setAll(close, closeOtherTabs, closeAllTabs, closeTabsToTheLeft, closeTabsToTheRight);
        }
    }
}
