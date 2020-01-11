package org.glavo.viewer.gui;

import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import org.glavo.viewer.util.FontUtils;
import org.glavo.viewer.util.ImageUtils;

import static org.glavo.viewer.gui.Viewer.resource;

public class ViewerToolBar extends ToolBar {
    private Viewer viewer;

    public ViewerToolBar(Viewer viewer) {
        this.viewer = viewer;

        Button openFile = new Button(null, new ImageView(ImageUtils.openFileImage));
        openFile.setOnAction(event -> viewer.openFile());
        Tooltip openFileTip = new Tooltip(resource.getString("openFileButton.tooltip"));
        openFileTip.setFont(FontUtils.getUiFont());
        openFile.setTooltip(openFileTip);

        Button openFolder = new Button(null, new ImageView(ImageUtils.openFolderImage));
        openFolder.setOnAction(event -> viewer.openFolder());
        Tooltip openFolderTip = new Tooltip(resource.getString("openFolderButton.tooltip"));
        openFolderTip.setFont(FontUtils.getUiFont());
        openFolder.setTooltip(openFolderTip);

        this.getItems().addAll(openFile, openFolder);
    }
}
