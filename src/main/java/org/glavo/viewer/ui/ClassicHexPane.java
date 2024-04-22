/*
 * Copyright 2024 Glavo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.glavo.viewer.ui;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import kala.collection.primitive.ByteSeq;
import org.glavo.viewer.util.HexText;

import java.util.Objects;

public class ClassicHexPane extends ScrollPane implements HexPane {

    private final long size;

    private final HexText hex;
    private final TextArea textArea1;
    private final TextArea textArea2;
    private final TextArea textArea3;

    private final BorderPane statusBar;
    private final Label statusLabel;

    private final BytesBar bytesBar;

    public ClassicHexPane(ByteSeq seq) {
        this.hex = new HexText(seq);
        this.size = seq.size();
        textArea1 = new TextArea(hex.rowHeaderText);
        textArea2 = new TextArea(hex.bytesText);
        textArea3 = new TextArea(hex.asciiString);

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

        statusBar = new BorderPane();

        statusLabel = new Label(" ");
        statusBar.setLeft(statusLabel);

        bytesBar = new BytesBar((int) size);
        bytesBar.setMaxHeight(statusLabel.getMaxHeight());
        bytesBar.setPrefWidth(200);
        statusBar.setRight(bytesBar);
    }

    @Override
    public void select(int offset, int length) {
        int rowIndex = offset / HexText.BYTES_PER_ROW;
        int rows = textArea3.getText().length() / (HexText.BYTES_PER_ROW + 1);

        textArea2.positionCaret(HexText.calcBytesTextPosition(offset));
        textArea2.selectPositionCaret(HexText.calcBytesTextPosition(offset + length) - 1);

        textArea3.positionCaret(HexText.calcAsciiTextPosition(offset));
        textArea3.selectPositionCaret(HexText.calcAsciiTextPosition(offset + length));

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

        bytesBar.select(offset, length);
    }

    private void initTextArea() {
        textArea1.setPrefColumnCount(6);
        textArea2.setPrefColumnCount(46);
        textArea3.setPrefColumnCount(16);

        int rowCount = hex.rowHeaderText.length() / 9 + 1;
        textArea1.setPrefRowCount(rowCount);
        textArea2.setPrefRowCount(rowCount);
        textArea3.setPrefRowCount(rowCount);

        registerAsciiPaneMenu(textArea1);
        registerHexPaneMenu(textArea2);
        registerTextPaneMenu(textArea3);

        textArea1.setEditable(false);
        textArea2.setEditable(false);
        textArea3.setEditable(false);

        textArea1.setStyle("-fx-text-fill: grey;");
    }

    private void registerAsciiPaneMenu(TextArea area) {
        ContextMenu menu = new ContextMenu();
        area.setContextMenu(menu);
    }

    private void registerHexPaneMenu(TextArea area) {
        ContextMenu menu = new ContextMenu();
        area.setContextMenu(menu);
    }

    private void registerTextPaneMenu(TextArea area) {
        ContextMenu menu = new ContextMenu();
        area.setContextMenu(menu);
    }

    @Override
    public void setStatus(String status) {
        this.statusLabel.setText(Objects.requireNonNullElse(status, " "));
    }

    @Override
    public Node getStatusBar() {
        return statusBar;
    }
}
