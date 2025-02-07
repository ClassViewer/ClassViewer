package org.glavo.viewer.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.input.*;
import javafx.stage.Stage;
import org.glavo.viewer.file.types.FileType;
import org.glavo.viewer.util.ImageUtils;
import org.glavo.viewer.util.logging.Log;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public final class Viewer extends Application {
    public static final String TITLE = "ClassViewer";
    public static final ResourceBundle resource = ResourceBundle.getBundle("org.glavo.viewer.ViewerResources");

    public static final int DEFAULT_WIDTH = 1200;
    public static final int DEFAULT_HEIGHT = 675;

    private Stage stage;
    private Scene scene;
    private ViewerMainPane pane;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        this.pane = new ViewerMainPane(this);

        this.scene = new Scene(pane, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        Stylesheet.setStylesheet(scene);

        enableDragAndDrop(scene);

        stage.setScene(scene);
        stage.setTitle(TITLE);
        stage.getIcons().add(ImageUtils.loadImage("/icons/spy16.png"));
        stage.getIcons().add(ImageUtils.loadImage("/icons/spy32.png"));
        stage.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN).match(event)) {
                ViewerTab tab = (ViewerTab) pane.getTabPane().getSelectionModel().getSelectedItem();
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
        stage.setOnShown(event -> Log.info("Show " + this));
        stage.setOnCloseRequest(event -> Log.info("Close " + this));
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        Log.shutdown();
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
                    pane.getMenuBar().updateRecentFiles();
                });
                task.startInNewThread();
            }
        } catch (Exception e) {
            ViewerAlert.logAndShowExceptionAlert(e);
        }
    }

    public void addTab(ViewerTab tab) {
        if (tab != null) {
            pane.getTabPane().getTabs().add(pane.getTabPane().getSelectionModel().getSelectedIndex() + 1, tab);
            pane.getTabPane().getSelectionModel().select(tab);
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
            Tab tab = tabs.getFirst();
            pane.getTabPane().getTabs().add(pane.getTabPane().getSelectionModel().getSelectedIndex() + 1, tab);
            pane.getTabPane().getSelectionModel().select(tab);
            return;
        }

        pane.getTabPane().getTabs().addAll(pane.getTabPane().getSelectionModel().getSelectedIndex() + 1, tabs);
    }

    public void removeTab(ViewerTab tab) {
        pane.getTabPane().getTabs().remove(tab);
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

    public ViewerTabPane getTabPane() {
        return pane.getTabPane();
    }

}
