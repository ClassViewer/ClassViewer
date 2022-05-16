package org.glavo.viewer.ui;

import javafx.scene.layout.StackPane;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.glavo.viewer.util.HexText;

public class FallbackHexPane extends StackPane implements HexPane {
    private final CodeArea area;

    public FallbackHexPane(byte[] array) {
        area = new CodeArea();

        area.getStylesheets().clear();
        area.setParagraphGraphicFactory(LineNumberFactory.get(area, i -> "%08X"));
        //area.setEditable(false);
        String text = new HexText(array).bytesText;
        area.replaceText(text);area.scrollToPixel(0, 0);
        VirtualizedScrollPane<CodeArea> scrollPane = new VirtualizedScrollPane<>(area);
        this.getChildren().add(scrollPane);


    }

    @Override
    public void select(int offset, int length) {
        // TODO
    }
}
