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

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;

import java.lang.ref.WeakReference;

public final class FileTreeView extends TreeView<String> {
    private final Viewer viewer;

    public FileTreeView(Viewer viewer) {
        this.viewer = viewer;
        this.setRoot(new TreeItem<>());
        this.setShowRoot(false);
        this.setCellFactory(tree -> new Cell());
    }

    @SuppressWarnings("FieldCanBeLocal")
    private static final class Cell extends TreeCell<String> {
        private WeakReference<TreeItem<String>> treeItemRef;

        private final InvalidationListener treeItemGraphicListener = observable -> updateDisplay(getItem(), isEmpty());
        private final InvalidationListener treeItemListener = new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                TreeItem<String> oldTreeItem = treeItemRef == null ? null : treeItemRef.get();
                if (oldTreeItem != null) {
                    oldTreeItem.graphicProperty().removeListener(weakTreeItemGraphicListener);
                }

                TreeItem<String> newTreeItem = getTreeItem();
                if (newTreeItem != null) {
                    newTreeItem.graphicProperty().addListener(weakTreeItemGraphicListener);
                    treeItemRef = new WeakReference<>(newTreeItem);
                }
            }
        };

        private final WeakInvalidationListener weakTreeItemGraphicListener = new WeakInvalidationListener(treeItemGraphicListener);
        private final WeakInvalidationListener weakTreeItemListener = new WeakInvalidationListener(treeItemListener);

        public Cell() {
            treeItemProperty().addListener(weakTreeItemListener);

            if (getTreeItem() != null) {
                getTreeItem().graphicProperty().addListener(weakTreeItemGraphicListener);
            }

            this.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2 && getTreeItem() instanceof FileTree fileTree) {
                    fileTree.onClick(((FileTreeView) getTreeView()).viewer);
                }
            });
        }

        private void updateDisplay(String item, boolean empty) {
            if (item == null || empty) {
                setText(null);
                setGraphic(null);
            } else {
                // update the graphic if one is set in the TreeItem
                TreeItem<String> treeItem = getTreeItem();
                Node graphic = treeItem == null ? null : treeItem.getGraphic();
                setText(item);
                setGraphic(graphic);
            }
        }

        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            updateDisplay(item, empty);
        }
    }
}
