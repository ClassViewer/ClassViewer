package org.glavo.viewer.ui;

import javafx.scene.Node;
import kala.tuple.primitive.IntTuple2;
import org.glavo.viewer.annotation.FXThread;

import java.util.function.Consumer;

public interface HexPane {
    default Node getNode() {
        return (Node) this;
    }

    void select(int offset, int length);

    @FXThread
    default void setOnSelect(Consumer<IntTuple2> consumer) {
    }

    default Node createBytesBar() {
        return null;
    }
}
