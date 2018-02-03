package org.glavo.viewer.gui.filetypes.binary;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import org.glavo.viewer.util.FontUtils;
import org.glavo.viewer.util.ImageUtils;

public final class TextPaneMenu extends ContextMenu {
    private TextArea textArea;

    public TextPaneMenu(TextArea textArea) {
        this.textArea = textArea;
        MenuItem copy = new MenuItem("_Copy");
        this.setStyle(FontUtils.setUIFont(this.getStyle()));
        copy.setMnemonicParsing(true);
        copy.setOnAction(e -> {
            javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(textArea.getSelectedText());
            clipboard.setContent(content);
        });
        copy.setGraphic(new ImageView(ImageUtils.copyImage));

        getItems().addAll(copy);
    }


}
