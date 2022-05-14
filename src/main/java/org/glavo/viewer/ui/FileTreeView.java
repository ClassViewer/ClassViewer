package org.glavo.viewer.ui;

import javafx.collections.ObservableList;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.glavo.viewer.file.*;
import org.glavo.viewer.file.types.ContainerFileType;
import org.glavo.viewer.file.types.CustomFileType;
import org.glavo.viewer.file.types.FolderType;
import org.glavo.viewer.resources.Images;

import java.util.logging.Level;

import static org.glavo.viewer.util.Logging.LOGGER;

public class FileTreeView extends TreeView<String> {
    public static TreeItem<String> fromTree(FileTree node, ContainerHandle handle) {
        FileTreeItem item = new FileTreeItem(node);
        item.setContainerHandle(handle);
        for (FileTree child : node.getChildren()) {
            item.getChildren().add(fromTree(child));
        }
        return item;
    }

    public static TreeItem<String> fromTree(FileTree node) {
        TreeItem<String> item = new FileTreeItem(node);
        for (FileTree child : node.getChildren()) {
            item.getChildren().add(fromTree(child));
        }
        return item;
    }

    public static void updateSubTree(TreeItem<String> tree) {
        if (tree instanceof FileTreeItem) {
            tree.getChildren().clear();
            for (FileTree child : ((FileTreeItem) tree).getFileTree().getChildren()) {
                tree.getChildren().add(fromTree(child));
            }
        }

    }

    private final Viewer viewer;

    public FileTreeView(Viewer viewer) {
        this.viewer = viewer;
        this.setOnMouseClicked(this::onMouseClicked);

        this.setRoot(new TreeItem<>());
        this.setShowRoot(false);
    }

    private void onMouseClicked(MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
            TreeItem<String> it = getSelectionModel().getSelectedItem();
            if (!(it instanceof FileTreeItem)) return;

            FileTreeItem item = ((FileTreeItem) it);

            FileTree node = ((FileTreeItem) it).getFileTree();

            if (!(node.getType() instanceof CustomFileType)) return;

            viewer.open(node.getPath());
        }
    }

    public static final class FileTreeItem extends TreeItem<String> {
        private final FileTree fileTree;
        private ContainerHandle containerHandle;

        public FileTreeItem(FileTree fileTree) {
            this.fileTree = fileTree;

            this.setGraphic(new ImageView(fileTree.getType().getImage()));
            this.setValue(fileTree.getText());
        }

        public FileTree getFileTree() {
            return fileTree;
        }

        private Boolean isLeaf;

        @Override
        public boolean isLeaf() {
            if (isLeaf == null) {
                FileType type = getFileTree().getType();
                if (type instanceof FolderType) {
                    return getChildren().isEmpty();
                } else {
                    isLeaf = !(type instanceof ContainerFileType);
                }
            }
            return isLeaf;
        }

        private boolean needToInit = true;

        @Override
        public ObservableList<TreeItem<String>> getChildren() {
            if (needToInit) {
                needToInit = false;
                if (containerHandle == null && !(getFileTree() instanceof FileTree.FolderNode) && !isLeaf()) {
                    FileTree node = this.getFileTree();
                    try {
                        Container container = Container.getContainer(node.getPath());
                        containerHandle = new ContainerHandle(container);
                        FileTree.buildFileTree(container, node);
                        updateSubTree(this);
                    } catch (Throwable e) {
                        LOGGER.log(Level.WARNING, "Failed to open container", e);
                    }

                    if (super.getChildren().isEmpty()) {
                        isLeaf = true;
                    }
                }
            }

            return super.getChildren();
        }

        public ContainerHandle getContainerHandle() {
            return containerHandle;
        }

        public void setContainerHandle(ContainerHandle containerHandle) {
            this.containerHandle = containerHandle;
        }
    }

    public static final class LoadingItem extends TreeItem<String> {
        public LoadingItem() {
            ProgressIndicator indicator = new ProgressIndicator();
            indicator.setPrefSize(16, 16);
            this.setGraphic(indicator);
        }

        public LoadingItem(String value) {
            this();
            this.setValue(value);
        }
    }

    public static final class FailedItem extends TreeItem<String> {
        public FailedItem() {
            this.setGraphic(new ImageView(Images.failed));
        }

        public FailedItem(String value) {
            this();
            this.setValue(value);
        }
    }
}
