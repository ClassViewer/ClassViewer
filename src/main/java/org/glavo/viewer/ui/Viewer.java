package org.glavo.viewer.ui;

import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
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
import org.glavo.viewer.file.containers.RootContainer;
import org.glavo.viewer.file.handles.FolderHandle;
import org.glavo.viewer.file.handles.PhysicalFileHandle;
import org.glavo.viewer.file.types.BinaryFileType;
import org.glavo.viewer.file.types.ContainerFileType;
import org.glavo.viewer.file.types.FolderType;
import org.glavo.viewer.file.types.TextFileType;
import org.glavo.viewer.resources.I18N;
import org.glavo.viewer.resources.Images;
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

    private final BorderPane root;

    private final Pane defaultText;
    private final MenuBar menuBar;
    private final TabPane tabPane;

    public Viewer(Stage stage, boolean isPrimary) {
        this.stage = stage;

        Config config = Config.getConfig();

        this.root = new BorderPane();
        this.tabPane = new TabPane();
        this.menuBar = createMenuBar();
        this.defaultText = createDefaultText();

        root.setTop(menuBar);
        root.setCenter(defaultText);

        tabPane.getTabs().addListener((InvalidationListener) o ->
                root.setCenter(tabPane.getTabs().isEmpty() ? defaultText : tabPane));
        tabPane.getSelectionModel().selectedItemProperty().addListener((o, oldValue, newValue) -> {
            if (newValue instanceof FileTab) {
                titleMessage.set(((FileTab) newValue).getPath().toString());
            } else {
                titleMessage.set(null);
            }
        });


        Scene scene = new Scene(root);
        stage.setWidth(config.getWindowSize().getWidth());
        stage.setHeight(config.getWindowSize().getHeight());
        if (config.getWindowSize().isMaximized()) {
            stage.setMaximized(true);
        }

        scene.getStylesheets().setAll(Stylesheet.getStylesheets());

        stage.setScene(scene);
        stage.getIcons().setAll(Images.logo32, Images.logo16);
        titleMessage.addListener((o, oldValue, newValue) -> {
            stage.setTitle(newValue == null ? "ClassViewer" : "ClassViewer - " + newValue);
        });
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
            openFileItem.setGraphic(new ImageView(Images.menuOpen));
            openFileItem.setOnAction(event -> openFile());

            MenuItem openFolderItem = new MenuItem(I18N.getString("menu.file.items.openFolder"));
            openFolderItem.setMnemonicParsing(true);
            openFolderItem.setOnAction(event -> openFolder());

            Menu openRecentMenu = new Menu(I18N.getString("menu.file.items.openRecent"));
            openRecentMenu.setMnemonicParsing(true);

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

    public void show() {
        getStage().show();
    }

    public File showFileChooser() {
        if (fileChooser == null) {
            fileChooser = new FileChooser();
            fileChooser.setTitle("Open file");
        }

        return fileChooser.showOpenDialog(getStage());
    }

    public File showDirectoryChooser() {
        if (directoryChooser == null) {
            directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Open folder");
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
            } else {
                throw new AssertionError();
            }
        } catch (Throwable e) {
            LOGGER.log(Level.WARNING, "Failed to open file " + path, e);
            if (resource != null) {
                resource.close();
            }
        }

    }
}
