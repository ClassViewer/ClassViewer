package org.glavo.viewer.ui;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.glavo.viewer.util.ByteList;
import org.glavo.viewer.util.HexText;

public class FallbackHexPane extends StackPane implements HexPane {
    private final CodeArea area;

    public FallbackHexPane(ByteList array) {
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
        String text = new HexText(array).bytesText;
        area.replaceText(text);
        area.scrollToPixel(0, 0);
        VirtualizedScrollPane<CodeArea> scrollPane = new VirtualizedScrollPane<>(area);
        this.getChildren().add(scrollPane);
    }

    @Override
    public void select(int offset, int length) {
        area.selectRange(HexText.calcBytesTextPosition(offset), HexText.calcBytesTextPosition(offset + length) - 1);
    }

}
