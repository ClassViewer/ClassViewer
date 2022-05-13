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
import org.glavo.viewer.file.FileType;
import org.glavo.viewer.resources.I18N;
import org.glavo.viewer.resources.Images;
import org.glavo.viewer.util.MappedList;

public class ViewerPane extends BorderPane {
    private static final double DEFAULT_DIVIDER_POSITION = 0.25;

    private final Viewer viewer;

    private final MenuBar menuBar;

    private final SplitPane mainPane;
    private final TabPane filesTabPane;
    private final TabPane sideBar;

    private final FileTreeView fileTreeView;

    public ViewerPane(Viewer viewer) {
        this.viewer = viewer;
        this.fileTreeView = new FileTreeView(viewer);

        this.menuBar = createMenuBar();

        this.filesTabPane = createFilesTabPane();
        this.sideBar = createSideBar();

        this.mainPane = new SplitPane(sideBar, filesTabPane);
        {
            double dp = Config.getConfig().getDividerPosition();
            if (dp <= 0 || dp >= 1) {
                dp = 0.25;
            }
            SplitPane.Divider divider = mainPane.getDividers().get(0);
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
                }, ((FileTab) newValue).sideBarProperty()));
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
            openFileItem.setGraphic(new ImageView(Images.menuOpen));
            openFileItem.setOnAction(event -> viewer.openFile());

            MenuItem openFolderItem = new MenuItem(I18N.getString("menu.file.items.openFolder"));
            openFolderItem.setMnemonicParsing(true);
            openFolderItem.setOnAction(event -> viewer.openFolder());

            Menu openRecentMenu = new Menu(I18N.getString("menu.file.items.openRecent"));
            openRecentMenu.setMnemonicParsing(true);
            Bindings.bindContent(openRecentMenu.getItems(), new MappedList<>(Config.getConfig().getRecentFiles(),
                    path -> {
                        MenuItem item = new MenuItem(path.toString(), new ImageView(FileType.detectFileType(path).getImage()));
                        item.setOnAction(event -> viewer.open(path));
                        return item;
                    }));

            fileMenu.getItems().setAll(openFileItem, openFolderItem, openRecentMenu);
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

    private TabPane createSideBar() {
        TabPane sideBar = new TabPane();
        sideBar.setSide(Side.LEFT);
        sideBar.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        sideBar.getStyleClass().add(TabPane.STYLE_CLASS_FLOATING);

        Tab treeTab = new Tab(I18N.getString("sideBar.fileList"));
        treeTab.setGraphic(new ImageView(Images.folder));
        treeTab.setContent(fileTreeView);

        Tab infoTab = new Tab(I18N.getString("sideBar.fileInfo"));
        infoTab.setGraphic(new ImageView(Images.fileStructure));
        StackPane emptyLabel = new StackPane(new Label(I18N.getString("sideBar.fileInfo.empty")));
        infoTab.setContent(emptyLabel);
        filesTabPane.getSelectionModel().selectedItemProperty().addListener((o, oldValue, newValue) -> {
            if (newValue instanceof FileTab) {
                infoTab.contentProperty().bind(Bindings.createObjectBinding(() -> {
                    Node bar = ((FileTab) newValue).getSideBar();
                    return bar == null ? emptyLabel : bar;
                }, ((FileTab) newValue).sideBarProperty()));
            } else {
                infoTab.contentProperty().unbind();
                infoTab.setContent(null);
            }
        });

        sideBar.getTabs().setAll(treeTab, infoTab);
        return sideBar;
    }

    private TabPane createFilesTabPane() {
        TabPane tabPane = new TabPane();
        tabPane.getSelectionModel().selectedItemProperty().addListener((o, oldValue, newValue) -> {
            if (newValue instanceof FileTab) {
                viewer.setTitleMessage(((FileTab) newValue).getPath().toString());
            } else {
                viewer.setTitleMessage(null);
            }
        });
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        try {
            //noinspection Since15
            tabPane.setTabDragPolicy(TabPane.TabDragPolicy.REORDER);
        } catch (Throwable ignored) {
        }

        return tabPane;
    }

    public TabPane getFilesTabPane() {
        return filesTabPane;
    }

    public FileTreeView getFileTreeView() {
        return fileTreeView;
    }

    public void selectFileInfoTab() {
        sideBar.getSelectionModel().select(1);
    }

    public void addFileTab(FileTab tab) {
        getFilesTabPane().getTabs().add(tab);
        getFilesTabPane().getSelectionModel().select(tab);

        if (tab.getSideBar() != null) {
            selectFileInfoTab();
        }
    }
}
