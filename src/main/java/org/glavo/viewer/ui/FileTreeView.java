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
import org.glavo.viewer.file.ContainerFileType;
import org.glavo.viewer.file.CustomFileType;
import org.glavo.viewer.file.types.folder.FolderType;
import org.glavo.viewer.resources.Images;

import java.util.ArrayList;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static org.glavo.viewer.util.Logging.LOGGER;

public class FileTreeView extends TreeView<String> {
    public static TreeItem<String> fromTree(OldFileTree node, ContainerHandle handle) {
        ContainerFileTreeItem item = new ContainerFileTreeItem(node);
        item.setContainerHandle(handle);
        for (OldFileTree child : node.getChildren()) {
            item.getChildren().add(fromTree(child));
        }
        return item;
    }

    private static void addAllFolderNode(Queue<OldFileTree> queue, Set<OldFileTree> children) {
        for (OldFileTree child : children) {
            if (!(child instanceof OldFileTree.FolderNode)) {
                break;
            }

            queue.add(child);
        }
    }

    public static TreeItem<String> fromTree(OldFileTree node) {
        TreeItem<String> res;
        if (node instanceof OldFileTree.FolderNode) {
            IntermediateFolderItem item = new IntermediateFolderItem(node);

            OldFileTree n = node;
            OldFileTree l;
            while (n.getChildren().size() == 1 && (l = n.getChildren().iterator().next()) instanceof OldFileTree.FolderNode) {
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

        for (OldFileTree child : node.getChildren()) {
            res.getChildren().add(fromTree(child));
        }
        return res;
    }

    public static void updateSubTree(TreeItem<String> tree) {
        if (tree instanceof FileTreeItem) {
            tree.getChildren().clear();
            for (OldFileTree child : ((FileTreeItem) tree).getFileTree().getChildren()) {
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

            OldFileTree node = ((FileTreeItem) it).getFileTree();

            if (!(node.getType() instanceof CustomFileType)) return;

            viewer.open(node.getPath());
        }
    }

    public static class FileTreeItem extends TreeItem<String> {
        private final OldFileTree fileTree;


        public FileTreeItem(OldFileTree fileTree) {
            this.fileTree = fileTree;

            this.setGraphic(new ImageView(fileTree.getType().getImage()));
            this.setValue(fileTree.getText());
        }

        public OldFileTree getFileTree() {
            return fileTree;
        }
    }

    public static class ContainerFileTreeItem extends FileTreeItem {
        private final ObjectProperty<ContainerHandle> containerHandle = new SimpleObjectProperty<>();

        public ContainerFileTreeItem(OldFileTree fileTree) {
            super(fileTree);
        }

        private boolean needToInit = !(getFileTree().getType() instanceof FolderType) && getContainerHandle() != null;

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

                OldFileTree node = this.getFileTree();
                try {
                    LOGGER.info("Expand " + node.getPath());
                    Container container = Container.getContainer(node.getPath());
                    setContainerHandle(new ContainerHandle(container));
                    OldFileTree.buildFileTree(container, node);
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
        private final ObservableList<OldFileTree> nodes = FXCollections.observableList(new ArrayList<>(1));

        public IntermediateFolderItem(OldFileTree node) {
            this();
            this.nodes.add(node);
        }

        public IntermediateFolderItem(OldFileTree... nodes) {
            this();
            this.nodes.addAll(nodes);
        }

        private IntermediateFolderItem() {
            this.setGraphic(new ImageView(FolderType.TYPE.getImage()));
            this.valueProperty().bind(Bindings.createStringBinding(() -> {
                if (nodes.size() == 1) {
                    return nodes.get(0).getText();
                }
                return nodes.stream().map(OldFileTree::getText).collect(Collectors.joining("/"));
            }, nodes));
        }

        public ObservableList<OldFileTree> getNodes() {
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
