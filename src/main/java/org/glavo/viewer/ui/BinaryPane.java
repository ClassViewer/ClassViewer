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
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import org.glavo.viewer.util.FXUtils;
import org.glavo.viewer.util.Schedulers;
import org.jetbrains.annotations.NotNull;

import java.lang.foreign.MemorySegment;

public final class BinaryPane {

    private final FileTab tab;

    private final ObjectProperty<@NotNull MemorySegment> data = new SimpleObjectProperty<>();
    private final ObjectProperty<@NotNull View> view = new SimpleObjectProperty<>();
    private final ObjectProperty<Node> fileInfoNode = new SimpleObjectProperty<>();

    // HexPane
    private StackPane hexPane;
    private HexPane hexPaneImpl;

    public BinaryPane(FileTab tab, MemorySegment data) {
        this.tab = tab;
        this.data.set(data);

        view.addListener((o, oldValue, newValue) -> {
            tab.sideBarProperty().unbind();
            tab.contentProperty().unbind();

            View view = getView();

            switch (view) {
                case INFO -> {
                    tab.setSideBar(null);
                    tab.contentProperty().bind(fileInfoNodeProperty());
                }
                case BINARY -> {
                    tab.setSideBar(null);
                    tab.setContent(getHexPane());
                }
                case SPLIT -> {
                    tab.sideBarProperty().bind(fileInfoNodeProperty());
                    tab.setContent(getHexPane());
                }

                default -> throw new AssertionError("Unknown view: " + view);
            }
        });
    }

    private Node getHexPane() {
        if (hexPane == null) {
            hexPane = new StackPane();

            InvalidationListener listener = observable -> {
                MemorySegment bytes = data.get();
                hexPane.getChildren().setAll(new ProgressIndicator());
                Schedulers.common().execute(() -> {
                    var hexPane = new ClassicHexPane(bytes);
                    FXUtils.runLater(() -> {
                        if (data.get() == bytes) {
                            hexPaneImpl = hexPane;
                            this.hexPane.getChildren().setAll(hexPane);
                        }
                    });
                });
            };

            listener.invalidated(data);
            data.addListener(listener);
        }

        return hexPane;
    }

    public FileTab getTab() {
        return tab;
    }

    public void select(long offset, long length) {
        if (hexPaneImpl != null) {
            hexPaneImpl.select((int) offset, (int) length);
        }
    }

    public ObjectProperty<MemorySegment> dataProperty() {
        return data;
    }

    public @NotNull MemorySegment getData() {
        return data.get();
    }

    public void setData(@NotNull MemorySegment data) {
        this.data.set(data);
    }

    public ObjectProperty<View> viewProperty() {
        return view;
    }

    public View getView() {
        return view.get();
    }

    public void setView(@NotNull View view) {
        this.view.set(view);
    }

    public ObjectProperty<Node> fileInfoNodeProperty() {
        return fileInfoNode;
    }

    public Node getFileInfoNode() {
        return fileInfoNode.get();
    }

    public void setFileInfoNode(Node fileInfoNode) {
        this.fileInfoNode.set(fileInfoNode);
    }

    public enum View {
        INFO, BINARY, SPLIT
    }
}
