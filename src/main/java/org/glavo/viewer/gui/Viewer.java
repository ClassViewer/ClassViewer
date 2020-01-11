package org.glavo.viewer.gui;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Tab;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.glavo.viewer.gui.filetypes.FileType;
import org.glavo.viewer.util.FontUtils;
import org.glavo.viewer.util.ImageUtils;
import org.glavo.viewer.util.Log;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public final class Viewer extends Application {
    public static final String TITLE = "ClassViewer";
    public static final ResourceBundle resource = ResourceBundle.getBundle("org.glavo.viewer.gui.ViewerResources");

    public static final int DEFAULT_WIDTH = 1080;
    public static final int DEFAULT_HEIGHT = 608;

    private Stage stage;
    private Scene scene;
    private BorderPane pane;

    private Pane defaultText;

    private ViewerTopBar topBar;
    private ViewerTabPane tabPane;

    @Override
    public void init() throws Exception {
        Application.setUserAgentStylesheet(Options.skin);
        Log.info("info " + this);
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        this.pane = new BorderPane();
        this.topBar = new ViewerTopBar(this);
        this.tabPane = new ViewerTabPane(this);
        this.defaultText = this.createDefaultText();

        pane.setTop(topBar);
        pane.setCenter(defaultText);
        pane.centerProperty().bind(Bindings.createObjectBinding(() -> {
            if (tabPane.getTabs().isEmpty()) {
                return defaultText;
            }
            return tabPane;
        }, tabPane.getTabs()));
        FontUtils.setUIFont(tabPane);

        this.scene = new Scene(pane, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        scene.getStylesheets().add(Viewer.class.getResource("/css/viewer.css").toExternalForm());

        enableDragAndDrop(scene);

        if (!Options.useSystemTilteBar) {
            stage.initStyle(StageStyle.UNDECORATED);
        }
        stage.setScene(scene);
        stage.setTitle(TITLE);
        stage.getIcons().add(ImageUtils.loadImage("/icons/spy16.png"));
        stage.getIcons().add(ImageUtils.loadImage("/icons/spy32.png"));
        stage.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN).match(event)) {
                ViewerTab tab = (ViewerTab) tabPane.getSelectionModel().getSelectedItem();
                if (tab != null) {
                    tab.showSearchBar();
                }
                event.consume();
            }
        });

        if (this.getParameters() != null && this.getParameters().getUnnamed() != null) {
            List<String> args = this.getParameters().getUnnamed();
            ArrayList<File> files = new ArrayList<>(args.size());
            for (String arg : args) {
                files.add(new File(arg));
            }
            javafx.application.Platform.runLater(() -> openFiles(files));
        }
        stage.show();
        stage.setOnCloseRequest(event -> {
            Log.info("close " + this);
        });
        Log.info("show " + this);
    }

    public void openFile() {
        File file = ViewerFileChooser.showFileChooser(stage);
        if (file != null) {
            try {
                openFile(file.toURI().toURL());
            } catch (MalformedURLException e) {
                ViewerAlert.logAndShowExceptionAlert(e);
            }
        }
    }

    public void openFolder() {
        File file = ViewerFileChooser.showDirectoryChooser(stage);
        if (file != null) {
            try {
                openFile(file.toURI().toURL());
            } catch (MalformedURLException e) {
                ViewerAlert.logAndShowExceptionAlert(e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void openFile(URL url) {
        openFile(null, url);
    }

    public void openFile(FileType type, URL url) {
        try {
            Log.info("Open file: " + url);
            if (url != null) {
                OpenFileTask task = new OpenFileTask(this, type, url);
                task.setOnSucceeded((ViewerTab tab) -> {
                    addTab(tab);
                    topBar.getMenuBar().updateRecentFiles();
                });
                task.startInNewThread();
            }
        } catch (Exception e) {
            ViewerAlert.logAndShowExceptionAlert(e);
        }
    }

    public void addTab(ViewerTab tab) {
        if (tab != null) {
            tabPane.getTabs().add(tabPane.getSelectionModel().getSelectedIndex() + 1, tab);
            tabPane.getSelectionModel().select(tab);
        }
    }

    public void openFiles(List<File> files) {
        ArrayList<URL> urls = new ArrayList<>(files.size());
        for (File file : files) {
            if (file != null) {
                try {
                    urls.add(file.toURI().toURL());
                } catch (MalformedURLException e) {
                    ViewerAlert.logAndShowExceptionAlert(e);
                }
            }
        }
        openUrls(urls);
    }

    public void openUrls(List<URL> urls) {
        OpenFilesTask task = new OpenFilesTask(this, urls);
        task.setOnSucceeded(this::addTabs);
    }

    public void addTabs(List<ViewerTab> tabs) {
        if (tabs == null || tabs.isEmpty()) {
            return;
        }

        if (tabs.size() == 1) {
            Tab tab = tabs.get(0);
            tabPane.getTabs().add(tabPane.getSelectionModel().getSelectedIndex() + 1, tab);
            tabPane.getSelectionModel().select(tab);
            return;
        }

        tabPane.getTabs().addAll(tabPane.getSelectionModel().getSelectedIndex() + 1, tabs);
    }

    public void removeTab(ViewerTab tab) {
        tabPane.getTabs().remove(tab);
    }

    private void enableDragAndDrop(Scene scene) {
        scene.setOnDragOver(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            } else {
                event.consume();
            }
        });
        // Dropping over surface
        scene.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                success = true;
                for (File file : db.getFiles()) {
                    //System.out.println(file.getAbsolutePath());
                    try {
                        openFile(file.toURI().toURL());
                    } catch (MalformedURLException e) {
                        ViewerAlert.logAndShowExceptionAlert(e);
                    }
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    public Stage getStage() {
        return stage;
    }

    public Scene getScene() {
        return scene;
    }

    public BorderPane getPane() {
        return pane;
    }

    public ViewerTopBar getTopBar() {
        return topBar;
    }

    public ViewerTabPane getTabPane() {
        return tabPane;
    }

    private Pane createDefaultText() {
        VBox pane = new VBox();
        pane.setAlignment(Pos.CENTER);

        Text openFileText = new Text(resource.getString("defaultText.openFile"));
        openFileText.setFill(Color.GRAY);
        Hyperlink openFileLink = new Hyperlink(topBar.getMenuBar().fileMenu.openFileItem.getAccelerator().getDisplayText());
        openFileLink.setOnAction(event -> openFile());

        Text openFolderText = new Text(resource.getString("defaultText.openFolder"));
        openFolderText.setFill(Color.GRAY);
        Hyperlink openFolderLink = new Hyperlink(topBar.getMenuBar().fileMenu.openFolderItem.getAccelerator().getDisplayText());
        openFolderLink.setOnAction(event -> openFolder());

        TextFlow text = new TextFlow(
                openFileText, new Text(" "), openFileLink, new Text("\n"),
                openFolderText, new Text(" "), openFolderLink
        );
        text.setTextAlignment(TextAlignment.CENTER);

        pane.getChildren().add(text);
        return pane;
    }
}
