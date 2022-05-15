package org.glavo.viewer.ui;

import javafx.scene.Node;

public interface HexPane {
    default Node getNode() {
        return (Node) this;
    }

    void select(int offset, int length);
}
