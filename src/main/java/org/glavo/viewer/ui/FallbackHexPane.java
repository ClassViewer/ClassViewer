package org.glavo.viewer.ui;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import kala.collection.primitive.ByteSeq;
import kala.tuple.primitive.IntTuple2;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.glavo.viewer.util.HexText;

import java.util.function.Consumer;

public class FallbackHexPane extends StackPane implements HexPane {
    private final CodeArea area;

    public FallbackHexPane(ByteSeq seq) {
        area = new CodeArea();

        area.getStylesheets().clear();
        area.setParagraphGraphicFactory(idx -> {
            String text = String.format(" %08X ", idx);
            Label label = new Label(text);
            label.getStyleClass().add("mono");
            label.setTextFill(Color.GREY);
            return label;
        });
        area.setEditable(false);
        String text = new HexText(seq).bytesText;
        area.replaceText(text);
        area.scrollToPixel(0, 0);
        VirtualizedScrollPane<CodeArea> scrollPane = new VirtualizedScrollPane<>(area);
        this.getChildren().add(scrollPane);
    }

    @Override
    public void select(int offset, int length) {
        area.selectRange(HexText.calcBytesTextPosition(offset), HexText.calcBytesTextPosition(offset + length) - 1);
    }

    @Override
    public Node getStatusBar() {
        return HexPane.super.getStatusBar();
    }
}
