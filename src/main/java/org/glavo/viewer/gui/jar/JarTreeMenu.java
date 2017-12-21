package org.glavo.viewer.gui.jar;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.glavo.viewer.gui.Viewer;
import org.glavo.viewer.gui.support.ImageUtils;
import org.glavo.viewer.util.Log;

import javax.swing.text.View;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.net.MalformedURLException;
import java.net.URL;

public class JarTreeMenu extends ContextMenu {
    private JarTreeView view;

    public JarTreeMenu(JarTreeView view) {
        this.view = view;

        MenuItem item1 = new MenuItem("Copy Path");
        item1.setGraphic(new ImageView(ImageUtils.copyImage));
        item1.setOnAction(e -> {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            JarTreeNode item = (JarTreeNode) view.getSelectionModel().getSelectedItem();

            StringSelection selection = new StringSelection(
                    String.format("jar:%s!%s", view.jarURL, item.path).replace('\\', '/')
            );
            clipboard.setContents(selection, null);
        });

        MenuItem item2 = new MenuItem("Open in New Window");
        item2.setOnAction(event -> {
            Viewer viewer = new Viewer();
            viewer.start(new Stage());
            JarTreeNode item = (JarTreeNode) view.getSelectionModel().getSelectedItem();
            try {
                viewer.openFileInThisThread(
                        new URL(String.format("jar:%s!%s", view.jarURL, item.path).replace('\\', '/'))
                );
            } catch (MalformedURLException e) {
                Log.log(e);
            }
        });

        this.getItems().addAll(item1, item2);
    }
}
