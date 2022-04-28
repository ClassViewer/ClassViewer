package org.glavo.viewer.ui;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SkinBase;
import javafx.scene.control.Skinnable;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import org.glavo.viewer.util.HexText;

public class HexPane extends ScrollPane {

    private final HexText hex;
    private final TextArea textArea1;
    private final TextArea textArea2;
    private final TextArea textArea3;

    public HexPane(HexText hex) {
        this.hex = hex;
        textArea1 = new TextArea(hex.rowHeaderText);
        textArea2 = new TextArea(hex.bytesText);
        textArea3 = new TextArea(hex.asciiString);

        //textArea1.select
        initTextArea();

        HBox hbox = new HBox();

        hbox.getChildren().addAll(textArea1, textArea2, textArea3);
        for (Node t : hbox.getChildren()) {
            ((Skinnable) t).skinProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    for (Node child : ((SkinBase<?>) newValue).getChildren()) {
                        if (child instanceof ScrollPane) {
                            ((ScrollPane) child).setHbarPolicy(ScrollBarPolicy.NEVER);
                        }
                    }
                }
            });
        }

        setContent(hbox);
    }

    public void select(int offset, int length) {
        int rowIndex = offset / HexText.BYTES_PER_ROW;
        int rows = textArea3.getText().length() / (HexText.BYTES_PER_ROW + 1);

        textArea2.positionCaret(calcBytesTextPosition(offset));
        textArea2.selectPositionCaret(calcBytesTextPosition(offset + length) - 1);

        textArea3.positionCaret(calcAsciiTextPosition(offset));
        textArea3.selectPositionCaret(calcAsciiTextPosition(offset + length));

        double height = getHeight();
        double textHeight = textArea2.getHeight();

        double vvalue = (((double) rowIndex) / rows * textHeight / (textHeight - height) - height / 2 / textHeight);

        if (Double.isFinite(vvalue)) {
            if (vvalue < 0) {
                this.setVvalue(0);
            } else if (vvalue > 1) {
                this.setVvalue(1);
            } else {
                this.setVvalue(vvalue);
            }
        }
    }

    private void initTextArea() {
        textArea1.setPrefColumnCount(6);
        textArea2.setPrefColumnCount(46);
        textArea3.setPrefColumnCount(16);

        int rowCount = hex.rowHeaderText.length() / 9 + 1;
        textArea1.setPrefRowCount(rowCount);
        textArea2.setPrefRowCount(rowCount);
        textArea3.setPrefRowCount(rowCount);

        // textArea1.setContextMenu(new AsciiPaneMenu(textArea1));
        // textArea2.setContextMenu(new HexPaneMenu(textArea2));
        // textArea3.setContextMenu(new TextPaneMenu(textArea3));

        textArea1.setEditable(false);
        textArea2.setEditable(false);
        textArea3.setEditable(false);

        textArea1.setStyle("-fx-text-fill: grey;");
    }

    private int calcBytesTextPosition(int byteOffset) {
        int rowIndex = byteOffset / HexText.BYTES_PER_ROW;
        int colIndex = byteOffset % HexText.BYTES_PER_ROW;

        return (49 * rowIndex) + (colIndex * 3);
    }

    private int calcAsciiTextPosition(int byteOffset) {
        int rowIndex = byteOffset / HexText.BYTES_PER_ROW;
        int colIndex = byteOffset % HexText.BYTES_PER_ROW;

        return (17 * rowIndex) + (colIndex);
    }
}
