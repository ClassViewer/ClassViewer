package org.glavo.viewer.ui;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.glavo.viewer.util.HexText;

public class ModernHexPane extends StackPane implements HexPane {
    private final CodeArea bytesArea;
    private final CodeArea asciiArea;

    public ModernHexPane(byte[] array) {
        HexText text = new HexText(array);

        bytesArea = new CodeArea();
        asciiArea = new CodeArea();
        bytesArea.getStylesheets().clear();
        asciiArea.getStylesheets().clear();

        bytesArea.replaceText(text.bytesText);
        asciiArea.replaceText(text.asciiString);

        bytesArea.setParagraphGraphicFactory(LineNumberFactory.get(bytesArea, i -> "%08X"));

        VirtualizedScrollPane<CodeArea> bytesAreaScrollPane = new VirtualizedScrollPane<>(bytesArea, ScrollPane.ScrollBarPolicy.NEVER, ScrollPane.ScrollBarPolicy.NEVER);
        VirtualizedScrollPane<CodeArea> asciiAreaScrollPane = new VirtualizedScrollPane<>(asciiArea, ScrollPane.ScrollBarPolicy.NEVER, ScrollPane.ScrollBarPolicy.NEVER);
        bytesArea.scrollToPixel(0, 0);
        asciiArea.scrollToPixel(0, 0);

        ScrollBar scrollBar = new ScrollBar();
        scrollBar.setOrientation(Orientation.VERTICAL);

        scrollBar.maxProperty().bind(bytesAreaScrollPane.totalHeightEstimateProperty());
        scrollBar.valueProperty().addListener((o, oldValue, newValue) -> {
            bytesAreaScrollPane.scrollYToPixel(newValue.doubleValue());
            asciiAreaScrollPane.scrollYToPixel(newValue.doubleValue());
        });

        EventHandler<ScrollEvent> handle = scrollEvent -> {
            scrollBar.fireEvent(scrollEvent);
            scrollEvent.consume();
        };

        bytesAreaScrollPane.addEventFilter(ScrollEvent.ANY, handle);
        asciiAreaScrollPane.addEventFilter(ScrollEvent.ANY, handle);

        bytesAreaScrollPane.setPrefWidth(600);
        asciiAreaScrollPane.setPrefWidth(300);

        this.getChildren().add(new HBox(bytesAreaScrollPane, asciiAreaScrollPane, scrollBar));
    }

    @Override
    public void select(int offset, int length) {
        throw new UnsupportedOperationException(); // TODO
    }
}
