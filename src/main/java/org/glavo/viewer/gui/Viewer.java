package org.glavo.viewer.gui;

import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
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

/**
 * Main class.
 */
public class Viewer extends Application {

    private static final String TITLE = "ClassViewer";

    public static final int DEFAULT_WIDTH = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 7 * 4;
    public static final int DEFAULT_HEIGHT = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 5 * 3;

    public static Cmd cmd = new Cmd();

    private Stage stage;
    private BorderPane root;
    private MyMenuBar menuBar;
    private Font defaultFont = FontUtils.uiFont;

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        root = new BorderPane();
        root.setTop(createMenuBar());
        root.setCenter(createTabPane());

        Scene scene = new Scene(root, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        scene.getStylesheets().add("editor.css");
        enableDragAndDrop(scene);

        stage.setScene(scene);
        stage.setTitle(TITLE);
        stage.getIcons().add(ImageUtils.loadImage("/spy16.png"));
        stage.getIcons().add(ImageUtils.loadImage("/spy32.png"));

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

        menuBar.setOnOpenFile(this::onOpenFile);
        menuBar.setOnNewWindow(this::openNewWindow);
        //menuBar.setUseSystemMenuBar(true);

        return menuBar;
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

    private void openFile(File file) {
        try {
            openFile(file.toURI().toURL());
        } catch (MalformedURLException e) {
            e.printStackTrace(System.err);
        }
    }

    private OpenFileTask makeOpenFileTask(URL url) {
        Tab tab = createTab(url);
        OpenFileTask task = new OpenFileTask(url);

        task.setOnSucceeded((OpenFileResult ofr) -> {
            if (ofr.fileType == FileType.JAVA_JAR) {
                JarTreeView treeView = new JarTreeView(ofr.url, ofr.jarRootNode);
                treeView.setOpenClassHandler(this::openClassInJar);
                tab.setContent(treeView.getTreeView());
            } else {
                ParsedViewerPane viewerPane = new ParsedViewerPane(ofr.fileRootNode, ofr.hexText);
                tab.setGraphic(FileType.JAVA_CLASS.imageView());
                tab.setContent(viewerPane);
            }

            RecentFiles.INSTANCE.add(ofr.fileType, url);
            menuBar.updateRecentFiles();
        });

        task.setOnFailed((Throwable err) -> {
            Text errMsg = new Text(err.toString());
            tab.setContent(errMsg);
        });

        return task;
    }

    private void openFileInThisThread(URL url) {
        makeOpenFileTask(url).run();
    }

    private void openFile(URL url) {
        makeOpenFileTask(url).startInNewThread();
    }

    private void openClassInJar(String url) {
        try {
            openFile(new URL(url));
        } catch (MalformedURLException e) {
            e.printStackTrace(System.err);
        }
    }


    public static void main(String[] args) {
        launch(Viewer.class, cmd.parse(args));
    }

}
