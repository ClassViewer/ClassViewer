package org.glavo.viewer;

import javafx.scene.layout.BorderPane;
import org.glavo.viewer.gui.Viewer;

public final class ViewerPane extends BorderPane {
    private final Viewer viewer;

    public ViewerPane(Viewer viewer) {
        this.viewer = viewer;
    }

    public Viewer getViewer() {
        return viewer;
    }
}
