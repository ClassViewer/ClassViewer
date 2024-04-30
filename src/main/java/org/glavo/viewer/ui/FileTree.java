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
package org.glavo.viewer.ui;

import javafx.collections.ObservableList;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import kala.function.CheckedSupplier;
import org.glavo.viewer.annotation.FXThread;
import org.glavo.viewer.file.*;
import org.glavo.viewer.resources.I18N;
import org.glavo.viewer.resources.Images;
import org.glavo.viewer.util.FXUtils;
import org.glavo.viewer.util.Schedulers;
import org.glavo.viewer.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;


import static org.glavo.viewer.util.logging.Logger.LOGGER;

public final class FileTree extends TreeItem<String> {

    private static List<FileTree> createNodes(List<TypedVirtualFile> files) {
        return files.stream()
                .sorted(Comparator.comparing(TypedVirtualFile::isDirectory).reversed().thenComparing(TypedVirtualFile::getFileName))
                .map(file -> new FileTree(file, false))
                .toList();
    }

    private final TypedVirtualFile file;

    @SuppressWarnings("FieldCanBeLocal")
    private ContainerHandle containerHandle;

    private final boolean isRootNode;

    @FXThread
    private boolean needLoad;

    @FXThread
    private boolean isLoading = false;

    private final ImageView imageView = new ImageView();

    public FileTree(TypedVirtualFile file, boolean isRootNode) {
        this(file, file.getFileName(), isRootNode);
    }

    public FileTree(TypedVirtualFile file, String name, boolean isRootNode) {
        this.file = file;
        this.isRootNode = isRootNode;
        this.setValue(name);
        this.setGraphic(imageView);
        imageView.setImage(file.type().getImage());
        this.needLoad = file.isDirectory() || file.isContainer();
    }

    @FXThread
    public static void setFailed(TreeItem<?> item, Throwable exception) {
        ImageView view = new ImageView();
        item.setGraphic(view);
        setFailed(view, exception);
    }

    @FXThread
    private static void setFailed(ImageView view, Throwable exception) {
        view.setImage(Images.failed);
        Tooltip.install(view, new Tooltip(StringUtils.getStackTrace(exception)));
    }

    @FXThread
    public static void setLoading(TreeItem<?> item) {
        setLoading(item, ProgressIndicator.INDETERMINATE_PROGRESS);
    }

    @FXThread
    public static ProgressIndicator setLoading(TreeItem<?> item, double process) {
        var progressIndicator = new ProgressIndicator(process);
        progressIndicator.setPrefSize(16, 16);
        item.setGraphic(progressIndicator);
        return progressIndicator;
    }

    public TypedVirtualFile getFile() {
        return file;
    }

    private ObservableList<TreeItem<String>> getRawChildren() {
        return super.getChildren();
    }

    @Override
    public ObservableList<TreeItem<String>> getChildren() {
        if (needLoad) {
            needLoad = false;
            load();
        }

        return getRawChildren();
    }

    @Override
    public boolean isLeaf() {
        return !needLoad && getRawChildren().isEmpty();
    }

    void onClick(Viewer viewer) {
        if (!isRootNode && file.type() instanceof CustomFileType) {
            viewer.open(file);
        }
    }

    private Runnable loadDirectory(TypedVirtualFile file) throws IOException {
        List<TypedVirtualFile> files = file.listFiles();
        if (!this.isRootNode && files.size() == 1 && files.getFirst().isDirectory()) {
            var nameList = new ArrayList<String>();

            TypedVirtualFile current = file;
            List<TypedVirtualFile> currentFiles = files;

            while (currentFiles.size() == 1 && currentFiles.getFirst().isDirectory()) {
                nameList.add(current.getFileName());
                current = currentFiles.getFirst();
                currentFiles = current.listFiles();
            }

            FileTree currentTree = new FileTree(current, String.join("/", nameList), false);
            currentTree.getChildren().setAll(createNodes(currentFiles));
            currentTree.setExpanded(this.isExpanded());

            return () -> {
                var parentChildren = this.getParent().getChildren();

                int idx = parentChildren.indexOf(this);
                if (idx >= 0) {
                    parentChildren.set(idx, currentTree);
                }
            };
        }

        List<FileTree> subNodes = createNodes(files);
        return () -> {
            this.getChildren().setAll(createNodes(files));
            this.setGraphic(imageView);
        };
    }

    private Runnable loadContainer() throws IOException {
        Container container = file.file().getContainer();
        container.lock();
        try {
            Container subContainer = container.getSubContainer(file);
            containerHandle = new ContainerHandle(subContainer);
            containerHandle.setOnForceClose(() -> FXUtils.runInFx(() -> this.getParent().getChildren().remove(this)));
            if (subContainer.hasMultiRoots()) {
                var files = subContainer.getRootDirectories().stream()
                        .map(TypedVirtualFile::of)
                        .toList();
                return () -> {
                    this.getChildren().setAll(createNodes(files));
                    this.setGraphic(imageView);
                };
            } else {
                return loadDirectory(TypedVirtualFile.of(subContainer.getRootDirectory()));
            }
        } finally {
            container.unlock();
        }
    }

    @FXThread
    private void load() {
        if (isLoading) {
            return;
        }
        isLoading = true;

        setLoading(this);

        CompletableFuture.supplyAsync((CheckedSupplier<Runnable, IOException>)
                        () -> file.isDirectory() ? loadDirectory(this.getFile()) : loadContainer(), Schedulers.io())
                .whenCompleteAsync((action, exception) -> {
                    isLoading = false;
                    if (exception == null) {
                        if (action != null) {
                            action.run();
                        }
                    } else {
                        LOGGER.warning("Failed to load file: " + file, exception);

                        setFailed(imageView, exception);
                        setGraphic(imageView);
                    }
                }, Schedulers.javafx());
    }
}
