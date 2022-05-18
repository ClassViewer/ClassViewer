package org.glavo.viewer.ui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
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
import org.glavo.viewer.file.types.folder.FolderType;
import org.glavo.viewer.resources.Images;

import java.util.ArrayList;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static org.glavo.viewer.util.Logging.LOGGER;

public class FileTreeView extends TreeView<String> {
    public static TreeItem<String> fromTree(FileTree node, ContainerHandle handle) {
        ContainerFileTreeItem item = new ContainerFileTreeItem(node);
        item.setContainerHandle(handle);
        for (FileTree child : node.getChildren()) {
            item.getChildren().add(fromTree(child));
        }
        return item;
    }

    private static void addAllFolderNode(Queue<FileTree> queue, Set<FileTree> children) {
        for (FileTree child : children) {
            if (!(child instanceof FileTree.FolderNode)) {
                break;
            }

            queue.add(child);
        }
    }

    public static TreeItem<String> fromTree(FileTree node) {
        TreeItem<String> res;
        if (node instanceof FileTree.FolderNode) {
            IntermediateFolderItem item = new IntermediateFolderItem(node);

            FileTree n = node;
            FileTree l;
            while (n.getChildren().size() == 1 && (l = n.getChildren().iterator().next()) instanceof FileTree.FolderNode) {
                item.getNodes().add(l);
                n = l;
            }

            res = item;
            node = n;
        } else {
            res = node.getType() instanceof ContainerFileType
                    ? new ContainerFileTreeItem(node)
                    : new FileTreeItem(node);
        }

        for (FileTree child : node.getChildren()) {
            res.getChildren().add(fromTree(child));
        }
        return res;
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

    public static class FileTreeItem extends TreeItem<String> {
        private final FileTree fileTree;


        public FileTreeItem(FileTree fileTree) {
            this.fileTree = fileTree;

            this.setGraphic(new ImageView(fileTree.getType().getImage()));
            this.setValue(fileTree.getText());
        }

        public FileTree getFileTree() {
            return fileTree;
        }
    }

    public static class ContainerFileTreeItem extends FileTreeItem {
        private final ObjectProperty<ContainerHandle> containerHandle = new SimpleObjectProperty<>();

        public ContainerFileTreeItem(FileTree fileTree) {
            super(fileTree);
        }

        private boolean needToInit = true;

        @Override
        public boolean isLeaf() {
            return !needToInit && getChildren().isEmpty();
        }

        @Override
        public ObservableList<TreeItem<String>> getChildren() {
            if (!needToInit) return super.getChildren();
            synchronized (this) {
                if (!needToInit) return super.getChildren();
                needToInit = false;

                FileTree node = this.getFileTree();
                try {
                    LOGGER.info("Expand " + node.getPath());
                    Container container = Container.getContainer(node.getPath());
                    setContainerHandle(new ContainerHandle(container));
                    FileTree.buildFileTree(container, node);
                    updateSubTree(this);
                } catch (Throwable e) {
                    LOGGER.log(Level.WARNING, "Failed to open container", e);
                }
            }
            return super.getChildren();
        }

        public ObjectProperty<ContainerHandle> containerHandleProperty() {
            return containerHandle;
        }

        public ContainerHandle getContainerHandle() {
            return containerHandle.get();
        }

        public void setContainerHandle(ContainerHandle containerHandle) {
            this.containerHandle.set(containerHandle);
        }
    }

    public static final class IntermediateFolderItem extends TreeItem<String> {
        private final ObservableList<FileTree> nodes = FXCollections.observableList(new ArrayList<>(1));

        public IntermediateFolderItem(FileTree node) {
            this();
            this.nodes.add(node);
        }

        public IntermediateFolderItem(FileTree... nodes) {
            this();
            this.nodes.addAll(nodes);
        }

        private IntermediateFolderItem() {
            this.setGraphic(new ImageView(FolderType.TYPE.getImage()));
            this.valueProperty().bind(Bindings.createStringBinding(() -> {
                if (nodes.size() == 1) {
                    return nodes.get(0).getText();
                }
                return nodes.stream().map(FileTree::getText).collect(Collectors.joining("/"));
            }, nodes));
        }

        public ObservableList<FileTree> getNodes() {
            return nodes;
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
