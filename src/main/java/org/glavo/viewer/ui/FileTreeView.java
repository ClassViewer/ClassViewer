package org.glavo.viewer.ui;

import javafx.scene.control.TreeView;

public class FileTreeView extends TreeView<FileTreeNode> {
    private Viewer viewer;

    public FileTreeView(Viewer viewer, FileTreeNode root) {
        super(root);
        this.viewer = viewer;
        this.setContextMenu(new FileTreeMenu(this));
        root.setExpanded(true);
    }

    public FileTreeNode getSelected() {
        return (FileTreeNode) getSelectionModel().getSelectedItem();
    }

    public Viewer getViewer() {
        return viewer;
    }
}
