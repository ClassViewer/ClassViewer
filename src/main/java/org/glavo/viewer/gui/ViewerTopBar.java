package org.glavo.viewer.gui;

import de.codecentric.centerdevice.MenuToolkit;
import javafx.scene.layout.VBox;
import org.glavo.viewer.util.PlatformUtils;

public final class ViewerTopBar extends VBox {

    private Viewer viewer;

    private ViewerTitleBar titleBar = null;
    private ViewerMenuBar menuBar;
    private ViewerToolBar toolBar;


    public ViewerTopBar(Viewer viewer) {
        this.viewer = viewer;
        menuBar = new ViewerMenuBar(viewer);
        toolBar = new ViewerToolBar(viewer);

        if (PlatformUtils.isOSX()) {
            menuBar.useSystemMenuBarProperty().set(true);
            MenuToolkit.toolkit().setApplicationMenu(menuBar.macDefaultMenu);
        }

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
