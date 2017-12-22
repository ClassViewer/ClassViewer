package org.glavo.viewer.gui.jar;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.glavo.viewer.gui.Viewer;
import org.glavo.viewer.gui.support.ImageUtils;
import org.glavo.viewer.util.Log;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.net.MalformedURLException;
import java.net.URL;

public class JarTreeMenu extends ContextMenu {
    private JarTreeView view;

    public JarTreeMenu(JarTreeView view) {
        this.view = view;
        setOnShowing(event -> updateMenu());
        addSeparator();
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
            if (view.getSelectedClass() != null) {
                JarTreeNode item = (JarTreeNode) view.getSelectionModel().getSelectedItem();
                try {
                    viewer.openFileInThisThread(
                            new URL(String.format("jar:%s!%s", view.jarURL, item.path).replace('\\', '/'))
                    );
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
            JarTreeNode item = (JarTreeNode) view.getSelectionModel().getSelectedItem();

            StringSelection selection = new StringSelection(
                    String.format("jar:%s!%s", view.jarURL, item.path).replace('\\', '/')
            );
            clipboard.setContents(selection, null);
        });
        getItems().add(it);
    }

    void addSeparator() {
        getItems().add(new SeparatorMenuItem());
    }

    void updateMenu() {
        getItems().clear();
        JarTreeNode node = view.getSelected();
        if (node != null) {
            if (view.getSelected().toString().endsWith(".class")) {
                addOpenInNewTabItem();
                addOpenInNewWindowItem();
                addSeparator();
            }
            addCopyPathItem();
        }
    }
}
