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

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.TreeItem;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import kala.function.CheckedFunction;
import kala.function.CheckedSupplier;
import org.glavo.viewer.Config;
import org.glavo.viewer.annotation.FXThread;
import org.glavo.viewer.file.*;
import org.glavo.viewer.file.roots.sftp.SftpRootContainer;
import org.glavo.viewer.resources.I18N;
import org.glavo.viewer.resources.Images;
import org.glavo.viewer.util.FXUtils;
import org.glavo.viewer.util.Schedulers;
import org.glavo.viewer.util.Stylesheet;
import org.glavo.viewer.util.WindowDimension;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import static org.glavo.viewer.util.logging.Logger.LOGGER;

public final class Viewer extends Control {
    private final Stage stage;
    private final boolean isPrimary;

    private final StringProperty titleMessage = new SimpleStringProperty();

    private static FileChooser fileChooser;
    private static DirectoryChooser directoryChooser;

    private final ObjectProperty<Node> fileSideBar = new SimpleObjectProperty<>();

    public Viewer(Stage stage, boolean isPrimary) {
        this.stage = stage;
        this.isPrimary = isPrimary;

        Config config = Config.getConfig();

        Scene scene = new Scene(this);
        stage.setWidth(config.getWindowSize().width());
        stage.setHeight(config.getWindowSize().height());
        if (config.getWindowSize().maximized()) {
            stage.setMaximized(true);
        }

        Stylesheet.setStylesheet(scene.getStylesheets());

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
                    if (file.exists()) {
                        open(TypedVirtualFile.of(file));
                    }
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });

        stage.setScene(scene);
        FXUtils.setIcons(stage);
        stage.titleProperty().bind(Bindings.createStringBinding(() -> {
            String message = titleMessage.get();
            return message == null ? "ClassViewer" : "ClassViewer - " + message;
        }, titleMessage));
        stage.show();

        stage.setOnCloseRequest(e -> {
            if (isPrimary) {
                config.setWindowSize(stage.isMaximized()
                        ? new WindowDimension(true, config.getWindowSize().width(), config.getWindowSize().height())
                        : new WindowDimension(false, stage.getWidth(), stage.getHeight()));
            }
        });
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new ViewerSkin(this);
    }

    public Stage getStage() {
        return stage;
    }

    public ViewerSkin getViewerSkin() {
        return (ViewerSkin) getSkin();
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public String getTitleMessage() {
        return titleMessage.get();
    }

    public StringProperty titleMessageProperty() {
        return titleMessage;
    }

    public void setTitleMessage(String titleMessage) {
        this.titleMessage.set(titleMessage);
    }

    public void show() {
        getStage().show();
    }

    @FXThread
    public File showFileChooser() {
        if (fileChooser == null) {
            fileChooser = new FileChooser();
            fileChooser.setTitle(I18N.getString("choose.file"));
        }

        return fileChooser.showOpenDialog(getStage());
    }

    @FXThread
    public File showDirectoryChooser() {
        if (directoryChooser == null) {
            directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle(I18N.getString("choose.folder"));
        }

        return directoryChooser.showDialog(getStage());
    }

    @FXThread
    public void openFile() {
        File file = showFileChooser();
        if (file != null) {
            open(TypedVirtualFile.of(file));
        }
    }

    @FXThread
    public void openFolder() {
        File file = showDirectoryChooser();
        if (file != null) {
            open(TypedVirtualFile.of(file));
        }
    }

    @FXThread
    public void open(TypedVirtualFile file) {
        if (file.isDirectory()) {
            LOGGER.info("Open folder " + file);
        } else {
            LOGGER.info("Open file " + file);
        }

        try {
            if (file.type() instanceof CustomFileType customFileType) {
                FileTab fileTab = customFileType.openTab(file.file());
                getViewerSkin().addFileTab(fileTab);
            } else if (file.type() instanceof DirectoryFileType || file.type() instanceof ContainerFileType) {
                FileTree root = new FileTree(file, true);
                root.setExpanded(true);
                getViewerSkin().getFileTreeView().getRoot().getChildren().add(root);
            } else {
                throw new AssertionError("Unsupported file type " + file.type());
            }

            // TODO: Config.getConfig().addRecentFile(file);
        } catch (Throwable e) {
            LOGGER.warning("Failed to open file " + file, e);
        }
    }

    @FXThread
    public void connect() {
        SftpDialog.Result result = new SftpDialog().showAndWait().orElse(null);
        if (result != null) {
            TreeItem<String> node = new TreeItem<>(result.root().toString() + result.initPath());
            FXUtils.setLoading(node);

            getViewerSkin().getFileTreeView().getRoot().getChildren().add(node);

            AtomicReference<SftpRootContainer> holder = new AtomicReference<>();

            CompletableFuture.supplyAsync(CheckedSupplier.of(() -> {
                SftpRootContainer container = SftpRootContainer.connect(result.root(), result.password());
                holder.set(container);
                return container;
            })).thenApplyAsync(CheckedFunction.of(container -> {
                return container.getFile(result.initPath());
            })).whenCompleteAsync((path, exception) -> {
                System.gc();

                if (exception == null) {
                    FileTree newTree = new FileTree(TypedVirtualFile.of(path), node.getValue(), true);
                    int idx = getViewerSkin().getFileTreeView().getRoot().getChildren().indexOf(node);
                    if (idx >= 0) {
                        getViewerSkin().getFileTreeView().getRoot().getChildren().set(idx, newTree);
                        newTree.setExpanded(true);
                    } else {
                        SftpRootContainer container = holder.get();
                        if (container != null) {
                            container.forceClose();
                        }
                    }
                } else {
                    LOGGER.warning("Failed to connect sftp server", exception);
                    FileTree.setFailed(node, exception);
                }
            }, Schedulers.javafx());
        }
    }
}
