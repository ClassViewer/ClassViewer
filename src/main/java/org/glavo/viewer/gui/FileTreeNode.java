package org.glavo.viewer.gui;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeItem;

import java.net.URL;
import java.util.function.Consumer;

public class FileTreeNode extends TreeItem<FileTreeNode> {
    private Consumer<ContextMenu> updateMenu = menu -> menu.getItems().clear();

    private String desc = "";
    private URL url = null;

    public FileTreeNode() {
        this.setValue(this);
    }

    public void updateMenu(ContextMenu menu) {
        if (updateMenu != null) {
            updateMenu.accept(menu);
        }
    }

    @Override
    public String toString() {
        return desc;
    }

    public Consumer<ContextMenu> getUpdateMenu() {
        return updateMenu;
    }

    public void setUpdateMenu(Consumer<ContextMenu> updateMenu) {
        this.updateMenu = updateMenu;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }
}
