package org.glavo.viewer.ui;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.glavo.viewer.Config;
import org.glavo.viewer.file.*;
import org.glavo.viewer.file.types.*;
import org.glavo.viewer.resources.I18N;
import org.glavo.viewer.resources.Images;
import org.glavo.viewer.util.MappedList;
import org.glavo.viewer.util.SilentlyCloseable;
import org.glavo.viewer.util.Stylesheet;
import org.glavo.viewer.util.WindowDimension;

import java.io.File;
import java.util.logging.Level;

import static org.glavo.viewer.util.Logging.LOGGER;

public final class Viewer {
    private static final ObservableList<Viewer> viewers = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());

    private final Stage stage;

    private final StringProperty titleMessage = new SimpleStringProperty();

    private static FileChooser fileChooser;
    private static DirectoryChooser directoryChooser;

    private final TabPane tabPane;
    private final TabPane sideBar;

    private final FileTreeView fileTreeView = new FileTreeView();
    private final ObjectProperty<Node> fileSideBar = new SimpleObjectProperty<>();

    public Viewer(Stage stage, boolean isPrimary) {
        this.stage = stage;

        Config config = Config.getConfig();

        BorderPane root = new BorderPane();

        Pane defaultText = createDefaultText();
        root.setTop(createMenuBar());
        root.setCenter(defaultText);

        this.tabPane = new TabPane();
        // tabPane.getTabs().addListener((InvalidationListener) o -> root.setCenter(tabPane.getTabs().isEmpty() ? defaultText : tabPane));
        tabPane.getSelectionModel().selectedItemProperty().addListener((o, oldValue, newValue) -> {
            if (newValue instanceof FileTab) {
                titleMessage.set(((FileTab) newValue).getPath().toString());
                fileSideBar.bind(((FileTab) newValue).sideBarProperty());
            } else {
                titleMessage.set(null);
                fileSideBar.unbind();
                fileSideBar.set(null);
            }
        });
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        try {
            //noinspection Since15
            tabPane.setTabDragPolicy(TabPane.TabDragPolicy.REORDER);
        } catch (Throwable ignored) {
        }

        this.sideBar = createSideBar();

        SplitPane centerPane = new SplitPane();

        Scene scene = new Scene(root);
        stage.setWidth(config.getWindowSize().getWidth());
        stage.setHeight(config.getWindowSize().getHeight());
        if (config.getWindowSize().isMaximized()) {
            stage.setMaximized(true);
        }

        scene.getStylesheets().setAll(Stylesheet.getStylesheets());

        stage.setScene(scene);
        stage.getIcons().setAll(Images.logo32, Images.logo16);
        stage.titleProperty().bind(Bindings.createStringBinding(() -> {
            String message = titleMessage.get();
            return message == null ? "ClassViewer" : "ClassViewer - " + message;
        }, titleMessage));
        stage.show();

        viewers.add(this);
        stage.setOnCloseRequest(e -> {
            if (isPrimary) {
                config.setWindowSize(stage.isMaximized()
                        ? new WindowDimension(true, config.getWindowSize().getWidth(), config.getWindowSize().getHeight())
                        : new WindowDimension(false, stage.getWidth(), stage.getHeight()));
            }

            viewers.remove(this);
        });
    }

    public Stage getStage() {
        return stage;
    }

    private Pane createDefaultText() {
        Text openFileText = new Text(I18N.getString("defaultText.openFile"));
        openFileText.setFill(Color.GRAY);
        Hyperlink openFileLink = new Hyperlink("Ctrl+O"); // new Hyperlink(topBar.getMenuBar().fileMenu.openFileItem.getAccelerator().getDisplayText());
        openFileLink.setOnAction(event -> openFile());

        Text openFolderText = new Text(I18N.getString("defaultText.openFolder"));
        openFolderText.setFill(Color.GRAY);
        Hyperlink openFolderLink = new Hyperlink("Ctrl+Shift+O"); // new Hyperlink(topBar.getMenuBar().fileMenu.openFolderItem.getAccelerator().getDisplayText());
        openFolderLink.setOnAction(event -> openFolder());

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
            openFileItem.setOnAction(event -> openFile());

            MenuItem openFolderItem = new MenuItem(I18N.getString("menu.file.items.openFolder"));
            openFolderItem.setMnemonicParsing(true);
            openFolderItem.setOnAction(event -> openFolder());

            Menu openRecentMenu = new Menu(I18N.getString("menu.file.items.openRecent"));
            openRecentMenu.setMnemonicParsing(true);
            Bindings.bindContent(openRecentMenu.getItems(), new MappedList<>(Config.getConfig().getRecentFiles(),
                    path -> {
                        MenuItem item = new MenuItem(path.toString(), new ImageView(FileType.detectFileType(path).getImage()));
                        item.setOnAction(event -> open(path));
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
        infoTab.contentProperty().bind(fileSideBar);

        sideBar.getTabs().setAll(treeTab, infoTab);
        return sideBar;
    }

    public void show() {
        getStage().show();
    }

    public File showFileChooser() {
        if (fileChooser == null) {
            fileChooser = new FileChooser();
            fileChooser.setTitle(I18N.getString("choose.file"));
        }

        return fileChooser.showOpenDialog(getStage());
    }

    public File showDirectoryChooser() {
        if (directoryChooser == null) {
            directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle(I18N.getString("choose.folder"));
        }

        return directoryChooser.showDialog(getStage());
    }

    private void openFile() {
        File file = showFileChooser();
        if (file != null) {
            open(FilePath.ofJavaPath(file.toPath()));
        }
    }

    private void openFolder() {
        File file = showDirectoryChooser();
        if (file != null) {
            open(FilePath.ofJavaPath(file.toPath(), true));
        }
    }

    public void open(FilePath path) {
        if (path.isDirectory()) {
            LOGGER.info("Open folder " + path);
        } else {
            LOGGER.info("Open file " + path);
        }

        FileType type = FileType.detectFileType(path);

        SilentlyCloseable resource = null;
        try {
            if (type instanceof ContainerFileType) {
                ContainerHandle handle = new ContainerHandle(Container.getContainer(path));
                resource = handle;

                FileTree.RootNode root = new FileTree.RootNode(FolderType.TYPE, path);
                FileTree.buildFileTree(handle.getContainer(), root);

                FileTab tab = new FileTab(FolderType.TYPE, path);
                tab.setContent(new FileTreeView(root));

                tab.setOnClosed(event -> handle.close());
                tabPane.getTabs().add(tab);
            } else if (type instanceof TextFileType) {
                throw new UnsupportedOperationException(); // TODO
            } else if (type instanceof BinaryFileType) {
                throw new UnsupportedOperationException(); // TODO
            } else if (type instanceof CustomFileType) {
                throw new UnsupportedOperationException(); // TODO
            } else {
                throw new AssertionError();
            }

            Config.getConfig().addRecentFile(path);
        } catch (Throwable e) {
            LOGGER.log(Level.WARNING, "Failed to open file " + path, e);
            if (resource != null) {
                resource.close();
            }
        }

    }
}
