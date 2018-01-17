package org.glavo.viewer.gui;

import javafx.scene.control.TreeView;
import org.glavo.viewer.util.FontUtils;

public class FileTreeView extends TreeView<FileTreeNode> {
    private Viewer viewer;

    public FileTreeView(Viewer viewer, FileTreeNode root) {
        super(root);
        this.viewer = viewer;
        this.setContextMenu(new FileTreeMenu(this));
        root.setExpanded(true);
        FontUtils.setUIFont(this);
    }

    public FileTreeNode getSelected() {
        return (FileTreeNode) getSelectionModel().getSelectedItem();
    }

    public Viewer getViewer() {
        return viewer;
    }
}
