package org.glavo.viewer.gui;

import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.glavo.viewer.gui.directory.DirectoryTreeNode;
import org.glavo.viewer.gui.directory.DirectoryTreeView;
import org.glavo.viewer.gui.jar.JarTreeView;
import org.glavo.viewer.gui.parsed.ParsedViewerPane;
import org.glavo.viewer.gui.support.*;
import org.glavo.viewer.util.Log;
import org.glavo.viewer.util.UrlUtils;
import org.glavo.viewer.gui.support.FontUtils;

import java.awt.Toolkit;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import static javafx.scene.control.TabPane.STYLE_CLASS_FLOATING;

/**
 * Main class.
 */
public class Viewer extends Application {

    private static final String TITLE = "ClassViewer";

    public static final int DEFAULT_WIDTH = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 7 * 4;
    public static final int DEFAULT_HEIGHT = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 5 * 3;

    public static Cmd cmd = new Cmd();

    static {
        FontUtils.init();
    }

    private Stage stage;
    private BorderPane root;
    private MyMenuBar menuBar;
    private MyToolBar toolBar;

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        root = new BorderPane();
        root.setTop(new VBox(
                createMenuBar(),
                createToolBar()
        ));
        root.setCenter(createTabPane());

        Scene scene = new Scene(root, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        scene.getStylesheets().add("editor.css");
        enableDragAndDrop(scene);

        stage.setScene(scene);
        stage.setTitle(TITLE);
        stage.getIcons().add(ImageUtils.loadImage("/icons/spy16.png"));
        stage.getIcons().add(ImageUtils.loadImage("/icons/spy32.png"));

        if (cmd.files != null) {
            for (String file : cmd.files) {
                try {
                    openFileInThisThread(new File(file).toURI().toURL());
                } catch (MalformedURLException e) {
                    Log.log(e);
                }
            }
        }

        stage.show();
    }

    private TabPane createTabPane() {
        TabPane tp = new TabPane();
        tp.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends Tab> observable, Tab oldTab, Tab newTab) -> {
                    if (newTab != null) {
                        URL url = (URL) newTab.getUserData();
                        stage.setTitle(TITLE + " - " + url);
                    }
                });
        tp.getStyleClass().add(STYLE_CLASS_FLOATING);
        return tp;
    }

    private Tab createTab(URL url) {
        Tab tab = new Tab();
        tab.setText(UrlUtils.getFileName(url));
        tab.setUserData(url);
        tab.setContent(new BorderPane(new ProgressBar()));
        ((TabPane) root.getCenter()).getTabs().add(tab);
        return tab;
    }

    private MenuBar createMenuBar() {
        menuBar = new MyMenuBar();

        menuBar.setOnOpenFileWithType(this::onOpenFile);
        menuBar.setOnOpenFile(this::onOpenFile);
        menuBar.setOnOpenFolder(this::onOpenFolder);
        menuBar.setOnNewWindow(this::openNewWindow);
        menuBar.setUseSystemMenuBar(true);

        return menuBar;
    }

    private ToolBar createToolBar() {
        toolBar = new MyToolBar(menuBar);

        return toolBar;
    }

    // http://www.java2s.com/Code/Java/JavaFX/DraganddropfiletoScene.htm
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
                    openFile(file);
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    private void openNewWindow() {
        Viewer newApp = new Viewer();
        // is this correct?
        newApp.start(new Stage());
    }

    private void onOpenFile(FileType ft, URL url) {
        if (url == null) {
            File file = MyFileChooser.showFileChooser(stage, ft);
            if (file != null) {
                openFile(file);
            }
        } else {
            openFile(url);
        }
    }


    private void onOpenFile(URL url) {
        if (url == null) {
            File file = MyFileChooser.showFileChooser(stage);
            if (file != null) {
                openFile(file);
            }
        } else {
            openFile(url);
        }
    }

    private void onOpenFolder(URL url) {
        if (url == null) {
            File file = MyFileChooser.showDirectoryChooser(stage);
            if (file != null) {
                openFile(file);
            }
        } else {
            openFile(url);
        }
    }

    private void openFile(File file) {
        try {
            openFile(file.toURI().toURL());
        } catch (MalformedURLException e) {
            Log.log(e);
        }
    }


    private OpenFileTask makeOpenFileTask(URL url) {

        OpenFileTask task = new OpenFileTask(url);

        task.setOnSucceeded((OpenFileResult ofr) -> {
            Tab tab = createTab(url);
            if (ofr.fileType == FileType.JAVA_JAR) {
                assert ofr.jarRootNode != null;
                JarTreeView treeView = new JarTreeView(ofr.url, ofr.jarRootNode);
                treeView.setOpenClassHandler(this::openClassInJar);
                tab.setGraphic(new ImageView(FileType.JAVA_JAR.icon));
                tab.setContent(treeView);
            } else if (ofr.fileType == FileType.FOLDER) {
                DirectoryTreeView treeView = new DirectoryTreeView(ofr.url, ofr.directoryTreeNode);
                treeView.setOpenClassHandler(this::openClassInJar);
                tab.setGraphic(new ImageView(FileType.FOLDER.icon));
                tab.setContent(treeView);
            } else {
                ParsedViewerPane viewerPane = new ParsedViewerPane(ofr.fileRootNode, ofr.hexText);
                tab.setGraphic(FileType.JAVA_CLASS.imageView());
                tab.setContent(viewerPane);
            }

            RecentFiles.INSTANCE.add(ofr.fileType, url);
            menuBar.updateRecentFiles();
        });

        task.setOnFailed((Throwable err) -> {
            MyAlert alert = MyAlert.mkAlert(err);
            alert.setTitle("Open file failed");
            alert.setHeaderText(err.getMessage());
            alert.setContentText(err.toString());
            alert.showAndWait();
        });

        return task;
    }

    public void openFileInThisThread(URL url) {
        makeOpenFileTask(url).run();
    }

    private void openFile(URL url) {
        makeOpenFileTask(url).startInNewThread();
    }

    private void openClassInJar(String url) {
        try {
            openFile(new URL(url));
        } catch (MalformedURLException e) {
            Log.log(e);
        }
    }

    public static void main(String[] args) {
        launch(Viewer.class, cmd.parse(args));
    }
}
