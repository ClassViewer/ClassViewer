package org.glavo.viewer.ui;

import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import org.glavo.viewer.util.ImageUtils;

import static org.glavo.viewer.ui.Viewer.resource;

public class ViewerToolBar extends ToolBar {
    private Viewer viewer;

    public ViewerToolBar(Viewer viewer) {
        this.viewer = viewer;

        Button openFile = new Button(null, new ImageView(ImageUtils.openFileImage));
        openFile.setOnAction(event -> viewer.openFile());
        Tooltip openFileTip = new Tooltip(resource.getString("openFileButton.tooltip"));
        openFile.setTooltip(openFileTip);

        Button openFolder = new Button(null, new ImageView(ImageUtils.openFolderImage));
        openFolder.setOnAction(event -> viewer.openFolder());
        Tooltip openFolderTip = new Tooltip(resource.getString("openFolderButton.tooltip"));
        openFolder.setTooltip(openFolderTip);

        this.getItems().addAll(openFile, openFolder);
    }
}
