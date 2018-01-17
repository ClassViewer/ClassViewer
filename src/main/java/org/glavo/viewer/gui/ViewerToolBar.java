package org.glavo.viewer.gui;

import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import org.glavo.viewer.util.ImageUtils;


public class ViewerToolBar extends ToolBar {
    private Viewer viewer;

    public ViewerToolBar(Viewer viewer) {
        this.viewer = viewer;

        Button openFile = new Button(null, new ImageView(ImageUtils.openFileImage));
        openFile.setOnAction(event -> viewer.openFile());
        openFile.setTooltip(new Tooltip("Open file"));

        Button openFolder = new Button(null, new ImageView(ImageUtils.openFolderImage));
        openFile.setOnAction(event -> viewer.openFile());
        openFile.setTooltip(new Tooltip("Open folder"));

        this.getItems().addAll(openFile, openFolder);
    }
}
