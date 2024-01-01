package org.glavo.viewer.ui;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import kala.collection.primitive.ByteSeq;
import org.glavo.viewer.resources.I18N;

public class EmptyHexPane extends StackPane implements HexPane {
    public EmptyHexPane(ByteSeq bytes) {
        this.getChildren().add(new Label(I18N.getString("file.tooLarge")));
    }

    @Override
    public void select(int offset, int length) {
        // do nothing
    }
}
