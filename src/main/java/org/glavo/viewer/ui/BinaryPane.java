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
import org.glavo.viewer.file.FileComponent;

public final class BinaryPane {
    private final byte[] data;

    private final ObjectProperty<FileComponent<?>> selectedFileComponent = new SimpleObjectProperty<>();
    private final ObjectProperty<Node> fileInfoNode = new SimpleObjectProperty<>();
    private final ObjectProperty<Node> hexPaneNode = new SimpleObjectProperty<>();

    public BinaryPane(byte[] data) {
        this.data = data;
    }

    public ObjectProperty<FileComponent<?>> selectedFileComponentProperty() {
        return selectedFileComponent;
    }

    public FileComponent<?> getSelectedFileComponent() {
        return selectedFileComponent.get();
    }

    public void setSelectedFileComponent(FileComponent<?> fileComponent) {
        selectedFileComponent.set(fileComponent);
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

    public ObjectProperty<Node> hexPaneNodeProperty() {
        return hexPaneNode;
    }

    public Node getHexPaneNode() {
        return hexPaneNode.get();
    }

    public void setHexPaneNode(Node hexPaneNode) {
        this.hexPaneNode.set(hexPaneNode);
    }
}
