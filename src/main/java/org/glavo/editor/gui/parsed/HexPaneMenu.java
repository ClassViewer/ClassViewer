package org.glavo.editor.gui.parsed;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class HexPaneMenu extends ContextMenu {
    private TextArea textArea;

    public HexPaneMenu(TextArea textArea) {
        this.textArea = textArea;
        MenuItem copy = new MenuItem("_Copy");
        copy.setMnemonicParsing(true);
        copy.setOnAction(e -> {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

            StringSelection s = new StringSelection(
                    textArea.getSelectedText().replace("\n", "")
            );

            clipboard.setContents(s, null);
        });

        getItems().addAll(copy);
    }


}
