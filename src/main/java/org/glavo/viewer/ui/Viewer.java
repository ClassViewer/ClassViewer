package org.glavo.viewer.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
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
import org.glavo.viewer.resources.I18N;
import org.glavo.viewer.resources.Images;
import org.glavo.viewer.util.Stylesheet;
import org.glavo.viewer.util.WindowDimension;

import java.io.File;

public final class Viewer {
    private static final ObservableList<Viewer> viewers = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());

    private final Stage stage;

    private static FileChooser fileChooser;
    private static DirectoryChooser directoryChooser;

    private final Pane defaultText;
    private final MenuBar menuBar;

    public Viewer(Stage stage, boolean isPrimary) {
        this.stage = stage;

        Config config = Config.getConfig();

        BorderPane root = new BorderPane();

        this.menuBar = createMenuBar();
        this.defaultText = createDefaultText();

        root.setTop(menuBar);
        root.setCenter(defaultText);


        Scene scene = new Scene(root);
        stage.setWidth(config.getWindowSize().getWidth());
        stage.setHeight(config.getWindowSize().getHeight());
        if (config.getWindowSize().isMaximized()) {
            stage.setMaximized(true);
        }

        scene.getStylesheets().setAll(Stylesheet.getStylesheets());

        stage.setScene(scene);
        stage.getIcons().setAll(Images.logo32, Images.logo16);

        stage.setTitle("ClassViewer");
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

            MenuItem openFolderItem = new MenuItem(I18N.getString("menu.file.items.openFolder"));
            openFolderItem.setMnemonicParsing(true);

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
}
