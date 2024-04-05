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
package org.glavo.viewer.file2;

import javafx.collections.ObservableList;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import org.glavo.viewer.file.ContainerFileType;
import org.glavo.viewer.file.FileType;
import org.glavo.viewer.resources.Images;

public final class FileTree extends TreeItem<String> {
    private final FileType type;
    private final VirtualFile path;

    private Status status;

    public FileTree(FileType type, VirtualFile path) {
        this.type = type;
        this.path = path;
    }

    public FileType getType() {
        return type;
    }

    public VirtualFile getPath() {
        return path;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        if (status != this.status) {
            this.status = status;

            this.setGraphic(switch (status) {
                case DEFAULT, UNEXPANDED -> new ImageView(type.getImage());
                case FAILED -> new ImageView(Images.failed);
                case LOADING -> {
                    ProgressIndicator indicator = new ProgressIndicator();
                    indicator.setPrefSize(16, 16);
                    yield indicator;
                }
            });
        }
    }

    private org.glavo.viewer.file.ContainerHandle containerHandle;
    private boolean needToInit = getType() instanceof ContainerFileType;

    public void setContainerHandle(org.glavo.viewer.file.ContainerHandle containerHandle) {
        this.containerHandle = containerHandle;
    }

    public org.glavo.viewer.file.ContainerHandle getContainerHandle() {
        return containerHandle;
    }

    @Override
    public ObservableList<TreeItem<String>> getChildren() {
        ObservableList<TreeItem<String>> children = super.getChildren();
        if (needToInit) {
            needToInit = false;

//            if (getType() instanceof FolderType) {
//                // TODO
//            } else if (getType() instanceof ContainerFileType t) {
//                try {
//                    LOGGER.info("Expand " + getPath());
//                    org.glavo.viewer.file.Container container = Container.getContainer(getPath());
//                    setContainerHandle(new ContainerHandle(container));
//                    // OldFileTree.buildFileTree(container, node);
//                } catch (Throwable e) {
//                    LOGGER.log(Level.WARNING, "Failed to open container", e);
//                }
//            } else {
//                throw new AssertionError();
//            }
        }

        return children;
    }

    @Override
    public boolean isLeaf() {
        return !needToInit && super.getChildren().isEmpty();
    }

    public enum Status {
        DEFAULT,
        FAILED,
        LOADING,
        UNEXPANDED
    }
}
