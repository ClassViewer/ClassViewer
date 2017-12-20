package org.glavo.viewer.gui.parsed;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import org.glavo.viewer.gui.support.ImageUtils;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class HexPaneMenu extends ContextMenu {

    public HexPaneMenu(TextArea textArea) {
        MenuItem copy = new MenuItem("_Copy");
        copy.setMnemonicParsing(true);
        copy.setOnAction(e -> {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

            StringSelection s = new StringSelection(
                    textArea.getSelectedText().replace("\n", "")
            );

            clipboard.setContents(s, null);
        });
        copy.setGraphic(new ImageView(ImageUtils.copyImage));

        getItems().addAll(copy);
    }


}
