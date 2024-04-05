/*
 * Copyright (C) 2024 Glavo. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
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

    private Status status = Status.DEFAULT;

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

    private ContainerHandle containerHandle;
    private boolean needToInit = getType() instanceof ContainerFileType;

    public void setContainerHandle(ContainerHandle containerHandle) {
        this.containerHandle = containerHandle;
    }

    public ContainerHandle getContainerHandle() {
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
