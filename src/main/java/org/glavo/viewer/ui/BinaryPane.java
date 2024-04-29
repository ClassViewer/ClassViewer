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
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import kala.collection.immutable.primitive.ImmutableByteArray;
import org.glavo.viewer.util.FXUtils;
import org.glavo.viewer.util.Schedulers;
import org.jetbrains.annotations.NotNull;

import java.lang.foreign.MemorySegment;

public final class BinaryPane {

    private final FileTab tab;

    private final ObjectProperty<@NotNull MemorySegment> data = new SimpleObjectProperty<>();
    private final ObjectProperty<@NotNull View> view = new SimpleObjectProperty<>();
    private final ObjectProperty<Node> fileInfoNode = new SimpleObjectProperty<>();

    private final BorderPane statusBar;
    private final Label statusLabel;
    private final Pane byteBar;

    // HexPane
    private StackPane hexPane;
    private ClassicHexPane hexPaneImpl;

    public BinaryPane(FileTab tab, MemorySegment data) {
        this.tab = tab;
        this.data.set(data);

        this.statusBar = new BorderPane();
        this.statusLabel = new Label(" ");
        this.byteBar = new Pane();
        this.byteBar.setMaxHeight(statusLabel.getMaxHeight());
        this.byteBar.setPrefWidth(200);
        this.statusBar.setLeft(statusLabel);
        this.statusBar.setRight(byteBar);

        tab.setStatusBar(statusBar);

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
                    ClassicHexPane classicHexPane = new ClassicHexPane(bytes);
                    FXUtils.runLater(() -> {
                        if (data.get() == bytes) {
                            hexPaneImpl = classicHexPane;
                            hexPane.getChildren().setAll(classicHexPane);
                        }
                    });
                });
            };

            listener.invalidated(data);
            data.addListener(listener);
        }

        return hexPane;
    }

    public void select(long offset, long length) {
        final long byteCount = data.get().byteSize();
        final double w = byteBar.getWidth() - 4;
        final double h = byteBar.getHeight();

        byteBar.getChildren().setAll(
                new Line(0, h / 2, w, h / 2),
                new Rectangle(w * offset / byteCount, 4, w * length / byteCount, h - 8));

        if (hexPaneImpl != null) {
            hexPaneImpl.select((int) offset, (int) length);
        }
    }

    public void setStatus(String text) {
        statusLabel.setText(text);
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
