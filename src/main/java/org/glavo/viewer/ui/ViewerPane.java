package org.glavo.viewer.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import org.glavo.viewer.resources.I18N;

public final class ViewerPane extends BorderPane {
    private final Pane defaultText;
    private final MenuBar menuBar;

    public ViewerPane() {
        this.menuBar = createMenuBar();
        this.defaultText = createDefaultText();

        this.setTop(menuBar);
        this.setCenter(defaultText);
    }

    private Pane createDefaultText() {
        Text openFileText = new Text(I18N.getString("defaultText.openFile"));
        openFileText.setFill(Color.GRAY);
        Hyperlink openFileLink = new Hyperlink("Ctrl+O"); // new Hyperlink(topBar.getMenuBar().fileMenu.openFileItem.getAccelerator().getDisplayText());
        // openFileLink.setOnAction(event -> openFile());

        Text openFolderText = new Text(I18N.getString("defaultText.openFolder"));
        openFolderText.setFill(Color.GRAY);
        Hyperlink openFolderLink = new Hyperlink("Ctrl+Shift+O"); // new Hyperlink(topBar.getMenuBar().fileMenu.openFolderItem.getAccelerator().getDisplayText());
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

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu(I18N.getString("menu.file"));
        {
            fileMenu.setMnemonicParsing(true);

            MenuItem openFileItem = new MenuItem(I18N.getString("menu.file.items.openFile"));
            openFileItem.setMnemonicParsing(true);

            MenuItem openFolderItem = new MenuItem(I18N.getString("menu.file.items.openFolder"));
            openFolderItem.setMnemonicParsing(true);

            Menu openRecentMenu = new Menu(I18N.getString("menu.file.items.openRecent"));
            openRecentMenu.setMnemonicParsing(true);

            fileMenu.getItems().setAll(openFileItem);
        }


        Menu helpMenu = new Menu(I18N.getString("menu.help"));
        {
            helpMenu.setMnemonicParsing(true);

            MenuItem aboutItem = new MenuItem(I18N.getString("menu.help.items.about"));

            helpMenu.getItems().setAll(aboutItem);
        }

        menuBar.getMenus().setAll(fileMenu, helpMenu);
        return menuBar;
    }
}
