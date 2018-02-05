package org.glavo.viewer.gui;

import javafx.scene.layout.VBox;

public final class ViewerTopBar extends VBox {

    private Viewer viewer;

    private ViewerTitleBar titleBar = null;
    private ViewerMenuBar menuBar;
    private ViewerToolBar toolBar;


    public ViewerTopBar(Viewer viewer) {
        this.viewer = viewer;

        menuBar = new ViewerMenuBar(viewer);
        toolBar = new ViewerToolBar(viewer);

        if (Options.useSystemTilteBar) {
            this.getChildren().addAll(menuBar, toolBar);
        } else {
            this.titleBar = new ViewerTitleBar(viewer);
            this.getChildren().addAll(titleBar, menuBar, toolBar);
        }
    }

    public Viewer getViewer() {
        return viewer;
    }

    public ViewerMenuBar getMenuBar() {
        return menuBar;
    }
}
