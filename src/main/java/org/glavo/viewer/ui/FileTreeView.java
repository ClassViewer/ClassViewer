package org.glavo.viewer.ui;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.glavo.viewer.file.*;
import org.glavo.viewer.file.types.BinaryFileType;
import org.glavo.viewer.file.types.ContainerFileType;
import org.glavo.viewer.file.types.FolderType;
import org.glavo.viewer.util.HexText;

import java.util.logging.Level;

import static org.glavo.viewer.util.Logging.LOGGER;

public class FileTreeView extends TreeView<FileTree> {
    public static TreeItem<FileTree> fromTree(FileTree node) {
        TreeItem<FileTree> item = new Node(node);

        for (FileTree child : node.getChildren()) {
            item.getChildren().add(fromTree(child));
        }
        return item;
    }

    public static void updateSubTree(TreeItem<FileTree> tree) {
        for (FileTree child : tree.getValue().getChildren()) {
            tree.getChildren().add(fromTree(child));
        }
    }

    private final Viewer viewer;

    public FileTreeView(Viewer viewer) {
        this.viewer = viewer;
        this.setCellFactory(view -> new Cell());
        this.setOnMouseClicked(this::onMouseClicked);

        this.setRoot(new TreeItem<>());
        this.setShowRoot(false);
    }

    private void onMouseClicked(MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
            TreeItem<FileTree> item = getSelectionModel().getSelectedItem();
            if (item == null) return;

            FileTree node = item.getValue();
            FileType type = node.getType();
            FilePath path = node.getPath();

            if (!(type instanceof BinaryFileType)) return;

            Container container = Container.getContainerOrNull(path.getParent());
            if (container == null) return;

            try {
                try (FileHandle handle = new FileHandle(container.getStub(path))) {
                    FileTab tab = new FileTab(type, path);
                    tab.setContent(new HexPane(new HexText(handle.readAllBytes())));

                    viewer.getPane().getFilesTabPane().getTabs().add(tab);
                }

            } catch (Throwable e) {
                LOGGER.log(Level.WARNING, "", e); // TODO
            }
        }
    }

    private static final class Cell extends TreeCell<FileTree> {
        @Override
        protected void updateItem(FileTree item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                setText(item.getText());
                setGraphic(new ImageView(item.getType().getImage()));
            }
        }
    }

    private static final class Node extends TreeItem<FileTree> {
        public Node() {
        }

        public Node(FileTree value) {
            super(value);
        }

        private Boolean isLeaf;

        @Override
        public boolean isLeaf() {
            if (isLeaf == null) {
                FileType type = getValue().getType();
                if (type instanceof FolderType) {
                    isLeaf = getChildren().isEmpty();
                } else {
                    isLeaf = !(type instanceof ContainerFileType);
                }
            }

            return isLeaf;
        }

        private boolean needToInit = !(getValue().getType() instanceof FolderType);

        @Override
        public ObservableList<TreeItem<FileTree>> getChildren() {
            if (needToInit && !isLeaf()) {
                needToInit = false;
                FileTree node = this.getValue();
                try {
                    Container container = Container.getContainer(node.getPath());
                    FileTree.buildFileTree(container, node);
                    updateSubTree(this);
                } catch (Throwable e) {
                    LOGGER.log(Level.WARNING, "Failed to open container", e);

                }

                if (super.getChildren().isEmpty()) {
                    isLeaf = true;
                }
            }

            return super.getChildren();
        }
    }
}
