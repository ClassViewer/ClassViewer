/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 Glavo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.glavo.viewer.ui;

import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.glavo.viewer.resources.I18N;
import org.glavo.viewer.resources.Images;

public final class ViewerSkin extends SkinBase<Viewer> {

    final Pane defaultText;
    final ViewerTabPane tabPane;

    ViewerSkin(Viewer viewer) {
        super(viewer);
        Stylesheet.setStylesheet(viewer.scene);

        // Top
        MenuBar menuBar = createMenuBar();

        // Center
        this.tabPane = new ViewerTabPane(viewer);
        this.defaultText = createDefaultText();


        // Root
        BorderPane rootPane = new BorderPane();

        rootPane.setTop(menuBar);
        rootPane.centerProperty().bind(Bindings.createObjectBinding(
                () -> tabPane.getTabs().isEmpty() ? defaultText : tabPane,
                tabPane.getTabs()));

        this.getChildren().add(rootPane);
    }

    private MenuBar createMenuBar() {
        Menu fileMenu = new Menu(I18N.getString("menu.file"));
        fileMenu.setMnemonicParsing(true);
        {
            MenuItem openFileItem = new MenuItem(I18N.getString("menu.file.items.openFile"));
            openFileItem.setMnemonicParsing(true);
            openFileItem.setGraphic(Images.createImageView("menu-open"));
            openFileItem.setOnAction(event -> getSkinnable().openFile());

            MenuItem openFolderItem = new MenuItem(I18N.getString("menu.file.items.openFolder"));
            openFolderItem.setMnemonicParsing(true);
            openFolderItem.setOnAction(event -> getSkinnable().openFolder());

            Menu openRecentMenu = new Menu(I18N.getString("menu.file.items.openRecent"));
            openRecentMenu.setMnemonicParsing(true);

            // TODO
//                Bindings.bindContent(openRecentMenu.getItems(), new MappedList<>(Config.getConfig().getRecentFiles(),
//                        file -> {
//                            MenuItem item = new MenuItem(file.toString(), new ImageView(file.type().getImage()));
//                            item.setOnAction(event -> getViewer().open(file));
//                            return item;
//                        }));

            fileMenu.getItems().setAll(openFileItem, openFolderItem, openRecentMenu);
        }


        Menu windowMenu = new Menu(I18N.getString("menu.window"));
        {
            // TODO
        }

        Menu helpMenu = new Menu(I18N.getString("menu.help"));
        helpMenu.setMnemonicParsing(true);
        {

            MenuItem aboutItem = new MenuItem(I18N.getString("menu.help.items.about"));

            helpMenu.getItems().setAll(aboutItem);
        }

        return new MenuBar(fileMenu, windowMenu, helpMenu);
    }

    private Pane createDefaultText() {
        var pane = new GridPane();
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(8);

        Text openFileText = new Text(I18N.getString("defaultText.openFile"));
        openFileText.setFill(Color.GRAY);
        Hyperlink openFileLink = new Hyperlink("Ctrl+O");
        openFileLink.setOnAction(event -> getSkinnable().openFile());

        Text openFolderText = new Text(I18N.getString("defaultText.openFolder"));
        openFolderText.setFill(Color.GRAY);
        Hyperlink openFolderLink = new Hyperlink("Ctrl+Shift+O");
        openFolderLink.setOnAction(event -> getSkinnable().openFolder());

        pane.add(openFileText, 0, 0);
        pane.add(openFileLink, 1, 0);
        pane.add(openFolderText, 0, 1);
        pane.add(openFolderLink, 1, 1);
        return pane;
    }
}
