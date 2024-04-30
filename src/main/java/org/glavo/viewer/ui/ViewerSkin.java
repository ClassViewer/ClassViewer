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

public final class ViewerSkin extends SkinBase<Viewer> {
    private static final double DEFAULT_DIVIDER_POSITION = 0.25;

    private final BorderPane root;

    private final MenuBar menuBar;

    private final SplitPane mainPane;
    private final TabPane filesTabPane;

    private final TabPane sideBar;
    private final Tab treeTab;
    private final Tab infoTab;

    private final FileTreeView fileTreeView;

    private final BorderPane statusBar;

    private final HBox statusButtons;

    public ViewerSkin(Viewer viewer) {
        super(viewer);
        this.root = new BorderPane();
        this.getChildren().add(root);

        this.menuBar = new MenuBar();
        {
            Menu fileMenu = new Menu(I18N.getString("menu.file"));
            {
                fileMenu.setMnemonicParsing(true);

                MenuItem openFileItem = new MenuItem(I18N.getString("menu.file.items.openFile"));
                openFileItem.setMnemonicParsing(true);
                openFileItem.setGraphic(new ImageView(Images.menuOpen));
                openFileItem.setOnAction(event -> getViewer().openFile());

                MenuItem openFolderItem = new MenuItem(I18N.getString("menu.file.items.openFolder"));
                openFolderItem.setMnemonicParsing(true);
                openFolderItem.setOnAction(event -> getViewer().openFolder());

                Menu openRecentMenu = new Menu(I18N.getString("menu.file.items.openRecent"));
                openRecentMenu.setMnemonicParsing(true);

                Bindings.bindContent(openRecentMenu.getItems(), new MappedList<>(Config.getConfig().getRecentFiles(),
                        file -> {
                            MenuItem item = new MenuItem(file.toString(), new ImageView(file.type().getImage()));
                            item.setOnAction(event -> getViewer().open(file));
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
        }

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

        this.fileTreeView = new FileTreeView(viewer);

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

        this.statusBar = new BorderPane();
        {
            statusBar.setPrefHeight(24);

            Label statusText = new Label();
            statusText.textProperty().bind(viewer.statusTextProperty());

            this.statusButtons = new HBox(4);

            statusBar.setLeft(statusText);
            statusBar.setRight(statusButtons);
        }

        FlowPane defaultPane = new FlowPane();{
            defaultPane.setAlignment(Pos.CENTER);

            Text openFileText = new Text(I18N.getString("defaultText.openFile"));
            openFileText.setFill(Color.GRAY);
            Hyperlink openFileLink = new Hyperlink("Ctrl+O");
            openFileLink.setOnAction(event -> getViewer().openFile());

            Text openFolderText = new Text(I18N.getString("defaultText.openFolder"));
            openFolderText.setFill(Color.GRAY);
            Hyperlink openFolderLink = new Hyperlink("Ctrl+Shift+O");
            openFolderLink.setOnAction(event -> getViewer().openFolder());


            Text remoteText = new Text(I18N.getString("defaultText.remote"));
            remoteText.setFill(Color.GRAY);
            Hyperlink remoteLink = new Hyperlink(I18N.getString("defaultText.remote.connect"));
            remoteLink.setOnAction(event -> getViewer().connect());

            TextFlow text = new TextFlow(
                    openFileText, new Text(" "), openFileLink, new Text("\n"),
                    openFolderText, new Text(" "), openFolderLink, new Text("\n"),
                    remoteText, new Text(" "), remoteLink
            );
            text.setTextAlignment(TextAlignment.LEFT);

            defaultPane.getChildren().add(text);
        }

        root.setTop(menuBar);
        root.setCenter(defaultPane);
        root.setBottom(statusBar);

        InvalidationListener listener = observable -> {
            if (filesTabPane.getTabs().isEmpty() && fileTreeView.getRoot().getChildren().isEmpty()) {
                root.setCenter(defaultPane);
                root.setBottom(null);
            } else {
                root.setCenter(mainPane);
                root.setBottom(statusBar);
            }
        };
        filesTabPane.getTabs().addListener(listener);
        fileTreeView.getRoot().getChildren().addListener(listener);
    }

    public Viewer getViewer() {
        return getSkinnable();
    }

    public BorderPane getRoot() {
        return root;
    }

    public TabPane getFilesTabPane() {
        return filesTabPane;
    }

    public FileTreeView getFileTreeView() {
        return fileTreeView;
    }

    public void selectFileTreeTab() {
        sideBar.getSelectionModel().select(treeTab);
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
