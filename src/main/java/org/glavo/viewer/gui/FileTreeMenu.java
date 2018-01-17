package org.glavo.viewer.gui;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.SeparatorMenuItem;
import org.glavo.viewer.util.Log;

public class FileTreeMenu extends ContextMenu {
    private FileTreeView view;

    public FileTreeMenu(FileTreeView view) {
        this.view = view;

        this.setOnShowing(event -> {
            Log.trace("Show Context Menu");
            FileTreeNode node = view.getSelected();
            if (node == null) {
                this.getItems().clear();
            } else {
                try {
                    node.updateMenu(view.getViewer(), this);
                } catch (Exception e) {
                    Log.error(e);
                    ViewerAlert.exceptionAlert(e);
                    this.getItems().clear();
                }
            }
        });

        this.getItems().add(new SeparatorMenuItem());
    }
}
