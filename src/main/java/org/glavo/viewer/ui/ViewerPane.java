package org.glavo.viewer.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import org.glavo.viewer.resources.I18N;
import org.glavo.viewer.util.FontUtils;

public final class ViewerPane extends BorderPane {
    private final Pane defaultText;

    public ViewerPane() {
        this.defaultText = createDefaultText();

        this.setCenter(defaultText);
    }

    private Pane createDefaultText() {
        Text openFileText = new Text(I18N.getString("defaultText.openFile"));
        openFileText.setFont(FontUtils.getUiFont());
        openFileText.setFill(Color.GRAY);
        Hyperlink openFileLink = new Hyperlink("Ctrl+O"); // new Hyperlink(topBar.getMenuBar().fileMenu.openFileItem.getAccelerator().getDisplayText());
        openFileLink.setFont(FontUtils.getUiFont());
        // openFileLink.setOnAction(event -> openFile());

        Text openFolderText = new Text(I18N.getString("defaultText.openFolder"));
        openFolderText.setFont(FontUtils.getUiFont());
        openFolderText.setFill(Color.GRAY);
        Hyperlink openFolderLink = new Hyperlink("Ctrl+Shift+O"); // new Hyperlink(topBar.getMenuBar().fileMenu.openFolderItem.getAccelerator().getDisplayText());
        openFolderLink.setFont(FontUtils.getUiFont());
        // openFolderLink.setOnAction(event -> openFolder());

        TextFlow text = new TextFlow(
                openFileText, new Text(" "), openFileLink, new Text("\n"),
                openFolderText, new Text(" "), openFolderLink
        );
        text.setTextAlignment(TextAlignment.LEFT);

        FlowPane pane = new FlowPane(text);
        pane.setAlignment(Pos.CENTER);
        return pane;
    }
}
