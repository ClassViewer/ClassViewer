package org.glavo.viewer.ui;

import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import org.glavo.viewer.file.FileTree;

public class FileTreeView extends TreeView<FileTree> {
    public FileTreeView(FileTree tree) {
        this.setCellFactory(view -> new TreeCell<>() {
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
        });
        this.setRoot(fromTree(tree));
    }

    private static TreeItem<FileTree> fromTree(FileTree node) {
        TreeItem<FileTree> item = new TreeItem<>(node);
        for (FileTree child : node.getChildren()) {
            item.getChildren().add(fromTree(child));
        }
        return item;
    }
}
