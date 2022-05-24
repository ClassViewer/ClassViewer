package org.glavo.viewer.ui;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.glavo.viewer.util.ByteList;
import org.glavo.viewer.util.HexText;


/*
 * WIP
 */
public class ModernHexPane extends StackPane implements HexPane {
    private final CodeArea bytesArea;
    private final CodeArea asciiArea;

    public ModernHexPane(ByteList array) {
        HexText text = new HexText(array);

        bytesArea = new CodeArea();
        asciiArea = new CodeArea();
        bytesArea.getStylesheets().clear();
        asciiArea.getStylesheets().clear();

        bytesArea.replaceText(text.bytesText);
        asciiArea.replaceText(text.asciiString);

        bytesArea.setParagraphGraphicFactory(idx -> {
            String t = String.format(" %08X ", idx);
            Label label = new Label(t);
            label.getStyleClass().add("mono");
            label.setTextFill(Color.GREY);
            return label;
        });

        bytesArea.prefWidthProperty().bind(bytesArea.totalWidthEstimateProperty());
        asciiArea.prefWidthProperty().bind(asciiArea.totalWidthEstimateProperty());

        VirtualizedScrollPane<CodeArea> bytesAreaScrollPane = new VirtualizedScrollPane<>(bytesArea);
        VirtualizedScrollPane<CodeArea> asciiAreaScrollPane = new VirtualizedScrollPane<>(asciiArea);
        bytesArea.scrollToPixel(0, 0);
        asciiArea.scrollToPixel(0, 0);

        bytesAreaScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        bytesAreaScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        asciiAreaScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        asciiAreaScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED );

        bytesAreaScrollPane.estimatedScrollYProperty().bindBidirectional(asciiAreaScrollPane.estimatedScrollYProperty());

        this.getChildren().add( new HBox(bytesAreaScrollPane, asciiAreaScrollPane));
    }

    @Override
    public void select(int offset, int length) {
        // TODO
    }
}
