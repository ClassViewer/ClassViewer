package org.glavo.viewer.gui.directory;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.glavo.viewer.gui.MyTreeNode;
import org.glavo.viewer.gui.Viewer;
import org.glavo.viewer.gui.jar.JarTreeNode;
import org.glavo.viewer.gui.support.ImageUtils;
import org.glavo.viewer.util.Log;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.net.MalformedURLException;
import java.net.URL;

public class DirectoryTreeMenu extends ContextMenu {
    private DirectoryTreeView view;

    public DirectoryTreeMenu(DirectoryTreeView view) {
        this.view = view;
        addSeparator();
        setOnShowing(e -> updateMenu());
    }


    void addOpenInNewTabItem() {
        MenuItem item = new MenuItem("Open in New Tab");

        item.setOnAction(event -> {
            String selectedClass = view.getSelectedClass();
            if (selectedClass != null && view.openClassHandler != null) {
                System.out.println(selectedClass);
                view.openClassHandler.accept(selectedClass);
            }
        });

        getItems().add(item);
    }

    void addOpenInNewWindowItem() {
        MenuItem it = new MenuItem("Open in New Window");
        it.setOnAction(event -> {
            Viewer viewer = new Viewer();
            viewer.start(new Stage());
            String c = view.getSelectedClass();
            if (c != null) {
                try {
                    viewer.openFileInThisThread(new URL(c));
                } catch (MalformedURLException e) {
                    Log.log(e);
                }
            }
        });
        getItems().add(it);
    }

    void addCopyPathItem() {
        MenuItem it = new MenuItem("Copy Path");
        it.setGraphic(new ImageView(ImageUtils.copyImage));
        it.setOnAction(e -> {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            MyTreeNode selected = (MyTreeNode) view.getSelectionModel().getSelectedItem();
            String classUrl;
            if (selected instanceof DirectoryTreeNode) {
                classUrl = "file:" + ((DirectoryTreeNode) selected).path;
                classUrl = classUrl.replace('\\', '/');
            } else {
                JarTreeNode node = (JarTreeNode) selected;
                classUrl = String.format("jar:file:%s!%s", node.jarPath, node.path).replace('\\', '/');
                if (node.isRoot) {
                    classUrl = classUrl.substring(0, classUrl.length() - 2);
                }
            }
            clipboard.setContents(new StringSelection(classUrl), null);
        });
        getItems().add(it);
    }

    void addSeparator() {
        getItems().add(new SeparatorMenuItem());
    }

    void updateMenu() {
        getItems().clear();
        TreeItem<MyTreeNode> node = view.getSelectionModel().getSelectedItem();
        if (node != null) {
            if (node.toString().endsWith(".class")) {
                addOpenInNewTabItem();
                addOpenInNewWindowItem();
                addSeparator();
            }
            addCopyPathItem();
        }
    }
}
