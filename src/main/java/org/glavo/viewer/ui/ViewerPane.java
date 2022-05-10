package org.glavo.viewer.ui;

import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
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
    private final Viewer viewer;

    private final Pane defaultText;

    private final MenuBar menuBar;

    private final SplitPane mainPane;
    private final TabPane filesTabPane;
    private final TabPane sideBar;

    private final FileTreeView fileTreeView = new FileTreeView();

    public ViewerPane(Viewer viewer) {
        this.viewer = viewer;

        this.defaultText = createDefaultText();
        this.menuBar = createMenuBar();

        this.mainPane = new SplitPane();
        this.filesTabPane = createFilesTabPane();
        this.sideBar = createSideBar();

        this.setCenter(defaultText);
    }

    private Pane createDefaultText() {
        Text openFileText = new Text(I18N.getString("defaultText.openFile"));
        openFileText.setFill(Color.GRAY);
        Hyperlink openFileLink = new Hyperlink("Ctrl+O"); // new Hyperlink(topBar.getMenuBar().fileMenu.openFileItem.getAccelerator().getDisplayText());
        openFileLink.setOnAction(event -> viewer.openFile());

        Text openFolderText = new Text(I18N.getString("defaultText.openFolder"));
        openFolderText.setFill(Color.GRAY);
        Hyperlink openFolderLink = new Hyperlink("Ctrl+Shift+O"); // new Hyperlink(topBar.getMenuBar().fileMenu.openFolderItem.getAccelerator().getDisplayText());
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

        Tab treeTab = new Tab("File Tree", new ImageView(Images.folder));
        treeTab.setContent(fileTreeView);

        Tab infoTab = new Tab("File Info");
        filesTabPane.getSelectionModel().selectedItemProperty().addListener((o, oldValue, newValue) -> {
            if (newValue instanceof FileTab) {
                infoTab.contentProperty().bind(Bindings.createObjectBinding(() -> {
                    Node bar = ((FileTab) newValue).getSideBar();
                    return bar == null ? new Label("Empty") : bar;
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
        // tabPane.getTabs().addListener((InvalidationListener) o -> root.setCenter(tabPane.getTabs().isEmpty() ? defaultText : tabPane));
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
}
