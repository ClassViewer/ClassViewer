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

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import org.glavo.viewer.Config;
import org.glavo.viewer.resources.I18N;
import org.glavo.viewer.resources.Images;
import org.glavo.viewer.util.MappedList;

public final class ViewerPane extends BorderPane {
    private static final double DEFAULT_DIVIDER_POSITION = 0.25;

    private final Viewer viewer;

    private final MenuBar menuBar;

    private final SplitPane mainPane;
    private final TabPane filesTabPane;

    private final TabPane sideBar;
    private final Tab treeTab;
    private final Tab infoTab;

    private final FileTreeView fileTreeView;

    public ViewerPane(Viewer viewer) {
        this.viewer = viewer;
        this.fileTreeView = new FileTreeView(viewer);

        this.menuBar = createMenuBar();

        this.filesTabPane = new TabPane();
        filesTabPane.getSelectionModel().selectedItemProperty().addListener((o, oldValue, newValue) -> {
            if (newValue instanceof FileTab) {
                viewer.setTitleMessage(((FileTab) newValue).getFile().toString());
            } else {
                viewer.setTitleMessage(null);
            }
        });
        filesTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        filesTabPane.setTabDragPolicy(TabPane.TabDragPolicy.REORDER);

        this.sideBar = new TabPane();
        {
            sideBar.setSide(Side.LEFT);
            sideBar.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
            sideBar.getStyleClass().add(TabPane.STYLE_CLASS_FLOATING);
            SplitPane.setResizableWithParent(sideBar, false);

            this.treeTab = new Tab(I18N.getString("sideBar.fileList"));
            treeTab.setGraphic(new ImageView(Images.folder));
            treeTab.setContent(fileTreeView);

            this.infoTab = new Tab(I18N.getString("sideBar.fileInfo"));
            infoTab.setGraphic(new ImageView(Images.fileStructure));
            StackPane emptyLabel = new StackPane(new Label(I18N.getString("sideBar.fileInfo.empty")));

            infoTab.setContent(emptyLabel);
            filesTabPane.getSelectionModel().selectedItemProperty().addListener((o, oldValue, newValue) -> {
                if (newValue instanceof FileTab fileTab) {
                    infoTab.contentProperty().bind(Bindings.createObjectBinding(() -> {
                        Node bar = ((FileTab) newValue).getSideBar();
                        return bar == null ? emptyLabel : bar;
                    }, fileTab.sideBarProperty()));
                } else {
                    infoTab.contentProperty().unbind();
                    infoTab.setContent(null);
                }
            });
            sideBar.getTabs().setAll(treeTab, infoTab);
        }

        this.mainPane = new SplitPane(sideBar, filesTabPane);
        {
            double dp = Config.getConfig().getDividerPosition();
            if (dp <= 0 || dp >= 1) {
                dp = DEFAULT_DIVIDER_POSITION;
            }

            SplitPane.Divider divider = mainPane.getDividers().getFirst();
            divider.setPosition(dp);

            if (viewer.isPrimary()) {
                Config.getConfig().dividerPositionProperty().bind(divider.positionProperty());
            }
        }

        HBox emptyStatusBar = new HBox();
        filesTabPane.getSelectionModel().selectedItemProperty().addListener((o, oldValue, newValue) -> {
            if (newValue instanceof FileTab) {
                bottomProperty().bind(Bindings.createObjectBinding(() -> {
                    Node bar = ((FileTab) newValue).getStatusBar();
                    return bar == null ? emptyStatusBar : bar;
                }, ((FileTab) newValue).statusBarProperty()));
            } else {
                bottomProperty().unbind();
                setBottom(null);
            }
        });

        this.setTop(menuBar);
        this.setCenter(createDefaultText());
        InvalidationListener l = new InvalidationListener() {
            @Override
            public void invalidated(Observable o) {
                filesTabPane.getTabs().removeListener(this);
                fileTreeView.getRoot().getChildren().removeListener(this);
                ViewerPane.this.setCenter(mainPane);
                if (fileTreeView.getRoot().getChildren().isEmpty()) {
                    selectFileInfoTab();
                }
            }
        };
        filesTabPane.getTabs().addListener(l);
        fileTreeView.getRoot().getChildren().addListener(l);

    }

    private Pane createDefaultText() {
        Text openFileText = new Text(I18N.getString("defaultText.openFile"));
        openFileText.setFill(Color.GRAY);
        Hyperlink openFileLink = new Hyperlink("Ctrl+O");
        openFileLink.setOnAction(event -> viewer.openFile());

        Text openFolderText = new Text(I18N.getString("defaultText.openFolder"));
        openFolderText.setFill(Color.GRAY);
        Hyperlink openFolderLink = new Hyperlink("Ctrl+Shift+O");
        openFolderLink.setOnAction(event -> viewer.openFolder());


        Text remoteText = new Text(I18N.getString("defaultText.remote"));
        remoteText.setFill(Color.GRAY);
        Hyperlink remoteLink = new Hyperlink(I18N.getString("defaultText.remote.connect"));
        remoteLink.setOnAction(event -> viewer.connect());

        TextFlow text = new TextFlow(
                openFileText, new Text(" "), openFileLink, new Text("\n"),
                openFolderText, new Text(" "), openFolderLink, new Text("\n"),
                remoteText, new Text(" "), remoteLink
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
            openFileItem.setGraphic(new ImageView(Images.menuOpen));
            openFileItem.setOnAction(event -> viewer.openFile());

            MenuItem openFolderItem = new MenuItem(I18N.getString("menu.file.items.openFolder"));
            openFolderItem.setMnemonicParsing(true);
            openFolderItem.setOnAction(event -> viewer.openFolder());

            Menu openRecentMenu = new Menu(I18N.getString("menu.file.items.openRecent"));
            openRecentMenu.setMnemonicParsing(true);

            Bindings.bindContent(openRecentMenu.getItems(), new MappedList<>(Config.getConfig().getRecentFiles(),
                    file -> {
                        MenuItem item = new MenuItem(file.toString(), new ImageView(file.type().getImage()));
                        item.setOnAction(event -> viewer.open(file));
                        return item;
                    }));

            fileMenu.getItems().setAll(openFileItem, openFolderItem, openRecentMenu);
        }


        Menu editMenu = new Menu(I18N.getString("menu.edit"));
        {

        }

        Menu windowMenu = new Menu(I18N.getString("menu.window"));
        {

        }

        Menu helpMenu = new Menu(I18N.getString("menu.help"));
        {
            helpMenu.setMnemonicParsing(true);

            MenuItem aboutItem = new MenuItem(I18N.getString("menu.help.items.about"));

            helpMenu.getItems().setAll(aboutItem);
        }

        menuBar.getMenus().setAll(fileMenu, editMenu, windowMenu, helpMenu);
        return menuBar;
    }

    public TabPane getFilesTabPane() {
        return filesTabPane;
    }

    public FileTreeView getFileTreeView() {
        return fileTreeView;
    }

    public void selectFileInfoTab() {
        sideBar.getSelectionModel().select(infoTab);
    }

    public void addFileTab(FileTab tab) {
        getFilesTabPane().getTabs().add(tab);
        getFilesTabPane().getSelectionModel().select(tab);

        if (tab.getSideBar() != null) {
            selectFileInfoTab();
        }
    }
}
