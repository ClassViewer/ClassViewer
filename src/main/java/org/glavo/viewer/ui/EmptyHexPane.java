package org.glavo.viewer.ui;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import org.glavo.viewer.resources.I18N;
import org.glavo.viewer.util.ByteList;

public class EmptyHexPane extends StackPane implements HexPane {
    public EmptyHexPane(ByteList bytes) {
        this.getChildren().add(new Label(I18N.getString("file.tooLarge")));
    }

    @Override
    public void select(int offset, int length) {
        // do nothing
    }
}
