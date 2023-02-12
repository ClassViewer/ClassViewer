package org.glavo.viewer.ui;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import kala.tuple.primitive.IntTuple2;

import java.util.function.Consumer;

public interface HexPane {
    default Node getNode() {
        return (Node) this;
    }

    void select(int offset, int length);

    default void setOnSelect(Consumer<IntTuple2> consumer) {

    }

    class BytesBar extends Pane {

        private final int byteCount;

        public BytesBar(int byteCount) {
            this.byteCount = byteCount;
        }

        public void select(int offset, int length) {
            getChildren().clear();

            final double w = getWidth() - 4;
            final double h = getHeight();

            getChildren().add(new Line(0, h / 2, w, h / 2));
            getChildren().add(new Rectangle(w * offset / byteCount, 4, w * length / byteCount, h - 8));
        }

    }

}
