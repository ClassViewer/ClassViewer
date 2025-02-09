package org.glavo.viewer.ui;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeView;
import org.glavo.viewer.util.logging.Log;

public class FileTreeView extends TreeView<FileTreeNode> {
    private final Viewer viewer;

    public FileTreeView(Viewer viewer, FileTreeNode root) {
        super(root);
        this.viewer = viewer;
        root.setExpanded(true);

        var contextMenu = new ContextMenu();
        contextMenu.setOnShowing(event -> {
            Log.trace("Show Context Menu");
            FileTreeNode node = this.getSelected();
            if (node == null) {
                contextMenu.getItems().clear();
            } else {
                try {
                    node.updateMenu(viewer, contextMenu);
                } catch (Exception e) {
                    ViewerAlert.logAndShowExceptionAlert(e);
                }
            }
        });
        contextMenu.getItems().add(new SeparatorMenuItem());
        this.setContextMenu(contextMenu);
    }

    public FileTreeNode getSelected() {
        return (FileTreeNode) getSelectionModel().getSelectedItem();
    }

    public Viewer getViewer() {
        return viewer;
    }
}
