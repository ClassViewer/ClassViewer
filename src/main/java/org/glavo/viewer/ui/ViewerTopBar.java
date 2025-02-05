package org.glavo.viewer.ui;

import javafx.scene.layout.VBox;
import org.glavo.viewer.Options;

public final class ViewerTopBar extends VBox {

    private Viewer viewer;

    private ViewerMenuBar menuBar;
    private ViewerToolBar toolBar;


    public ViewerTopBar(Viewer viewer) {
        this.viewer = viewer;

        menuBar = new ViewerMenuBar(viewer);
        toolBar = new ViewerToolBar(viewer);

        this.getChildren().addAll(menuBar, toolBar);
    }

    public Viewer getViewer() {
        return viewer;
    }

    public ViewerMenuBar getMenuBar() {
        return menuBar;
    }
}
