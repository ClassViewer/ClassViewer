package org.glavo.viewer.ui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.glavo.viewer.Config;
import org.glavo.viewer.file.*;
import org.glavo.viewer.resources.I18N;
import org.glavo.viewer.resources.Images;
import org.glavo.viewer.util.SilentlyCloseable;
import org.glavo.viewer.util.Stylesheet;
import org.glavo.viewer.util.TaskUtils;
import org.glavo.viewer.util.WindowDimension;

import java.io.File;
import java.util.logging.Level;

import static org.glavo.viewer.util.logging.Logger.LOGGER;

public final class Viewer {
    private static final ObservableList<Viewer> viewers = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());

    private final Stage stage;
    private final boolean isPrimary;

    private final StringProperty titleMessage = new SimpleStringProperty();

    private static FileChooser fileChooser;
    private static DirectoryChooser directoryChooser;

    private final ViewerPane pane;

    private final ObjectProperty<Node> fileSideBar = new SimpleObjectProperty<>();

    public Viewer(Stage stage, boolean isPrimary) {
        this.stage = stage;
        this.isPrimary = isPrimary;

        Config config = Config.getConfig();
        this.pane = new ViewerPane(this);

        Scene scene = new Scene(pane);
        stage.setWidth(config.getWindowSize().width());
        stage.setHeight(config.getWindowSize().height());
        if (config.getWindowSize().maximized()) {
            stage.setMaximized(true);
        }

        scene.getStylesheets().setAll(Stylesheet.getStylesheets());

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
                        open(FilePath.ofJavaPath(file.toPath(), file.isDirectory()));
                    }
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });

        stage.setScene(scene);
        stage.getIcons().setAll(Images.logo32, Images.logo16);
        stage.titleProperty().bind(Bindings.createStringBinding(() -> {
            String message = titleMessage.get();
            return message == null ? "ClassViewer" : "ClassViewer - " + message;
        }, titleMessage));
        stage.show();

        viewers.add(this);
        stage.setOnCloseRequest(e -> {
            if (isPrimary) {
                config.setWindowSize(stage.isMaximized()
                        ? new WindowDimension(true, config.getWindowSize().width(), config.getWindowSize().height())
                        : new WindowDimension(false, stage.getWidth(), stage.getHeight()));
            }

            viewers.remove(this);
        });
    }

    public Stage getStage() {
        return stage;
    }

    public ViewerPane getPane() {
        return pane;
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

    public File showFileChooser() {
        if (fileChooser == null) {
            fileChooser = new FileChooser();
            fileChooser.setTitle(I18N.getString("choose.file"));
        }

        return fileChooser.showOpenDialog(getStage());
    }

    public File showDirectoryChooser() {
        if (directoryChooser == null) {
            directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle(I18N.getString("choose.folder"));
        }

        return directoryChooser.showDialog(getStage());
    }

    public void openFile() {
        File file = showFileChooser();
        if (file != null) {
            open(FilePath.ofJavaPath(file.toPath()));
        }
    }

    public void openFolder() {
        File file = showDirectoryChooser();
        if (file != null) {
            open(FilePath.ofJavaPath(file.toPath(), true));
        }
    }

    public void open(FilePath path) {
        if (path.isDirectory()) {
            LOGGER.info("Open folder " + path);
        } else {
            LOGGER.info("Open file " + path);
        }

        FileType type = FileType.detectFileType(path);

        SilentlyCloseable resource = null;
        try {
            switch (type) {
                case ContainerFileType containerFileType -> {
                    //noinspection resource
                    ContainerHandle handle = new ContainerHandle(Container.getContainer(path));
                    resource = handle;

                    ObservableList<TreeItem<String>> treeItems = pane.getFileTreeView().getRoot().getChildren();

                    FileTreeView.LoadingItem loadingItem = new FileTreeView.LoadingItem(path.toString());
                    treeItems.add(loadingItem);

                    TaskUtils.submit(new Task<TreeItem<String>>() {
                        @Override
                        protected TreeItem<String> call() throws Exception {
                            OldFileTree.RootNode root = new OldFileTree.RootNode(type, path);
                            OldFileTree.buildFileTree(handle.getContainer(), root);
                            return FileTreeView.fromTree(root, handle);
                        }

                        @Override
                        protected void succeeded() {
                            int idx = treeItems.indexOf(loadingItem);
                            assert idx >= 0;

                            treeItems.set(idx, getValue());
                        }

                        @Override
                        protected void failed() {
                            LOGGER.warning("Failed to open container", getException());
                            int idx = treeItems.indexOf(loadingItem);
                            assert idx >= 0;
                            treeItems.set(idx, new FileTreeView.FailedItem(path.toString()));
                            handle.close();
                        }
                    });
                }
                case CustomFileType customFileType -> {
                    try (ContainerHandle containerHandle = new ContainerHandle(Container.getContainer(path.getParent()))) {
                        FileHandle handle = containerHandle.getContainer().openFile(path);
                        resource = handle;

                        FileTab tab = ((CustomFileType) type).openTab(handle);
                        getPane().addFileTab(tab);
                    }
                }
                default -> throw new AssertionError("Unhandled type: " + type);
            }

            Config.getConfig().addRecentFile(path);
        } catch (Throwable e) {
            LOGGER.warning("Failed to open file " + path, e);
            if (resource != null) {
                resource.close();
            }
        }

    }
}
