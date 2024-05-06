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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.glavo.viewer.Config;
import org.glavo.viewer.resources.I18N;
import org.glavo.viewer.resources.Images;
import org.glavo.viewer.util.MappedList;

public final class ViewerSkin extends SkinBase<Viewer> {
    private static final double DEFAULT_DIVIDER_POSITION = 0.25;

    private final BorderPane root;

    //
    // Top
    //
    private final MenuBar menuBar;

    //
    // Center
    //

    private final GridPane defaultPane;

    private final SplitPane mainPane;
    private final TabPane filesTabPane;

    private final TabPane sideBar;
    private final Tab treeTab;
    private final Tab infoTab;

    private final FileTreeView fileTreeView;

    //
    // Bottom
    //

    private final BorderPane statusBar;
    private final Label statusTextLabel;
    private final HBox statusButtons;

    public ViewerSkin(Viewer viewer) {
        super(viewer);
        this.root = new BorderPane();
        this.getChildren().add(root);

        //
        // Top
        //

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

        root.setTop(menuBar);

        //
        // Bottom
        //

        this.statusBar = new BorderPane();
        {
            statusBar.setPrefHeight(24);

            this.statusTextLabel = new Label();
            statusTextLabel.setPadding(new Insets(0, 0, 0, 4));

            this.statusButtons = new HBox(4);
            statusButtons.setAlignment(Pos.CENTER_RIGHT);

            statusBar.setLeft(statusTextLabel);
            statusBar.setRight(statusButtons);
        }

        root.setBottom(statusBar);

        //
        // Center
        //

        this.defaultPane = new GridPane();
        {
            defaultPane.setAlignment(Pos.CENTER);
            defaultPane.setHgap(8);

            var openFileText = new Label(I18N.getString("defaultText.openFile"));
            openFileText.setTextFill(Color.GRAY);
            Hyperlink openFileLink = new Hyperlink("Ctrl+O");
            openFileLink.setOnAction(event -> getViewer().openFile());

            var openFolderText = new Label(I18N.getString("defaultText.openFolder"));
            openFolderText.setTextFill(Color.GRAY);
            Hyperlink openFolderLink = new Hyperlink("Ctrl+Shift+O");
            openFolderLink.setOnAction(event -> getViewer().openFolder());

            var remoteText = new Label(I18N.getString("defaultText.remote"));
            remoteText.setTextFill(Color.GRAY);
            Hyperlink remoteLink = new Hyperlink(I18N.getString("defaultText.remote.connect"));
            remoteLink.setOnAction(event -> getViewer().connect());

            defaultPane.add(openFileText, 0, 0);
            defaultPane.add(openFileLink, 1, 0);
            defaultPane.add(openFolderText, 0, 1);
            defaultPane.add(openFolderLink, 1, 1);
            defaultPane.add(remoteText, 0, 2);
            defaultPane.add(remoteLink, 1, 2);
        }

        this.mainPane = new SplitPane();
        {
            this.filesTabPane = new TabPane();
            filesTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
            filesTabPane.setTabDragPolicy(TabPane.TabDragPolicy.REORDER);

            this.sideBar = new TabPane();
            {
                sideBar.setSide(Side.LEFT);
                sideBar.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
                sideBar.getStyleClass().add(TabPane.STYLE_CLASS_FLOATING);
                SplitPane.setResizableWithParent(sideBar, false);

                this.treeTab = new Tab(I18N.getString("sideBar.fileList"));
                this.fileTreeView = new FileTreeView(viewer);
                treeTab.setGraphic(new ImageView(Images.folder));
                treeTab.setContent(fileTreeView);

                this.infoTab = new Tab(I18N.getString("sideBar.fileInfo"));
                infoTab.setGraphic(new ImageView(Images.fileStructure));
                StackPane emptyLabel = new StackPane(new Label(I18N.getString("sideBar.fileInfo.empty")));

                infoTab.setContent(emptyLabel);

                sideBar.getTabs().setAll(treeTab, infoTab);

                filesTabPane.getSelectionModel().selectedItemProperty().addListener((o, oldValue, newValue) -> {
                    if (newValue instanceof FileTab fileTab) {
                        viewer.setTitleMessage(fileTab.getFile().toString());
                        statusTextLabel.textProperty().bind(fileTab.statusTextProperty());
                        infoTab.contentProperty().bind(Bindings.createObjectBinding(() -> {
                            Node bar = ((FileTab) newValue).getSideBar();
                            return bar == null ? emptyLabel : bar;
                        }, fileTab.sideBarProperty()));
                    } else {
                        viewer.setTitleMessage("");
                        statusTextLabel.textProperty().unbind();
                        statusTextLabel.setText("");
                        infoTab.contentProperty().unbind();
                        infoTab.setContent(null);
                    }
                });
            }

            mainPane.getItems().setAll(sideBar, filesTabPane);

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

        root.setCenter(defaultPane);

        //
        // ---
        ///

        InvalidationListener listener = observable -> {
            if (filesTabPane.getTabs().isEmpty() && fileTreeView.getRoot().getChildren().isEmpty()) {
                root.setCenter(defaultPane);
                root.setBottom(null);
                viewer.setTitleMessage("");
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
