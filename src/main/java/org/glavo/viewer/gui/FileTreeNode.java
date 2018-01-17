package org.glavo.viewer.gui;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.glavo.viewer.util.FontUtils;
import org.glavo.viewer.util.ImageUtils;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.net.URL;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class FileTreeNode extends TreeItem<FileTreeNode> {
    public static int comparePaths(FileTreeNode n1, FileTreeNode n2) {
        if (!n1.getChildren().isEmpty() && n2.getChildren().isEmpty()) {
            return -1;
        } else if (n1.getChildren().isEmpty() && !n2.getChildren().isEmpty()) {
            return 1;
        } else {
            return n1.toString().compareTo(n2.toString());
        }
    }

    private BiConsumer<Viewer, ContextMenu> updateMenu = (viewer, menu) -> menu.getItems().clear();

    private String desc = "";
    private URL url = null;

    public FileTreeNode() {
        this.setValue(this);
    }

    public void updateMenu(Viewer viewer, ContextMenu menu) {
        if (updateMenu != null) {
            updateMenu.accept(viewer, menu);
        }
    }

    public void setClassFileMenu(Viewer viewer, ContextMenu menu) {
        menu.getItems().clear();
        menu.getItems().addAll(
                copyPathMenu(),
                new SeparatorMenuItem(),
                openInNewTabMenu(viewer),
                openInNewWindowMenu(viewer)
        );
    }

    public MenuItem copyPathMenu() {
        MenuItem menu = new MenuItem("Copy path");
        menu.setStyle(FontUtils.setUIFont(menu.getStyle()));
        menu.setGraphic(new ImageView(ImageUtils.copyImage));
        menu.setOnAction(event -> {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection selection = new StringSelection(url.toString());
            clipboard.setContents(selection, null);
        });

        return menu;
    }

    public MenuItem openInNewTabMenu(Viewer viewer) {
        MenuItem menu = new MenuItem("Open in new Tab");
        menu.setStyle(FontUtils.setUIFont(menu.getStyle()));
        menu.setOnAction(event -> viewer.openFile(getUrl()));
        return menu;
    }

    public MenuItem openInNewWindowMenu(Viewer viewer) {
        MenuItem menu = new MenuItem("Open in new Window");
        menu.setStyle(FontUtils.setUIFont(menu.getStyle()));
        menu.setOnAction(event -> {
            Viewer newViewer = new Viewer();
            viewer.start(new Stage());
            viewer.openFile(getUrl());
        });

        return menu;
    }

    @Override
    public String toString() {
        return desc;
    }

    public BiConsumer<Viewer, ContextMenu> getUpdateMenu() {
        return updateMenu;
    }

    public void setUpdateMenu(BiConsumer<Viewer, ContextMenu> updateMenu) {
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
