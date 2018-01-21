package org.glavo.viewer.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.glavo.viewer.util.FontUtils;
import org.glavo.viewer.util.ImageUtils;
import org.glavo.viewer.util.Log;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public final class Viewer extends Application {
    public static final String TITLE = "ClassViewer";

    public static final int DEFAULT_WIDTH = 1000;
    public static final int DEFAULT_HEIGHT = 600;

    public static void main(String[] args) {
        Options.init();
        Application.launch(Viewer.class, args);
    }

    private Stage stage;
    private Scene scene;
    private BorderPane pane;

    private ViewerMenuBar menuBar;
    private ViewerTabPane tabPane;
    private ViewerToolBar toolBar;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        this.pane = new BorderPane();
        this.menuBar = new ViewerMenuBar(this);
        this.tabPane = new ViewerTabPane(this);
        this.toolBar = new ViewerToolBar(this);

        pane.setTop(new VBox(
                menuBar,
                toolBar
        ));
        pane.setCenter(tabPane);

        FontUtils.setUIFont(tabPane);

        this.scene = new Scene(pane, DEFAULT_WIDTH, DEFAULT_HEIGHT);

        enableDragAndDrop(scene);

        stage.setScene(scene);
        stage.setTitle(TITLE);
        stage.getIcons().add(ImageUtils.loadImage("/icons/spy16.png"));
        stage.getIcons().add(ImageUtils.loadImage("/icons/spy32.png"));

        stage.show();

        List<String> args = this.getParameters().getUnnamed();
        try {
            tabPane.getTabs().addAll(new OpenFileTask(this, args.stream().map(it -> {
                try {
                    return new File(it).toURI().toURL();
                } catch (MalformedURLException e) {
                    Log.error(e);
                    ViewerAlert.showExceptionAlert(e);
                    return null;
                }
            }).toArray(URL[]::new)).call());
        } catch (Exception e) {
            Log.error(e);
            ViewerAlert.showExceptionAlert(e);
        }
    }

    public void openFile() {
        File file = ViewerFileChooser.showFileChooser(stage);
        if (file != null) {
            try {
                openFile(file.toURI().toURL());
            } catch (MalformedURLException e) {
                Log.error(e);
                ViewerAlert.showExceptionAlert(e);
            }
        }
    }

    public void openFolder() {
        File file = ViewerFileChooser.showDirectoryChooser(stage);
        if (file != null) {
            try {
                openFile(file.toURI().toURL());
            } catch (MalformedURLException e) {
                Log.error(e);
                ViewerAlert.showExceptionAlert(e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void openFile(URL url) {
        try {
            Log.info("Open file: " + url);
            if (url != null) {
                OpenFileTask task = new OpenFileTask(this, url);
                task.setOnSucceeded(event -> {
                    List<Tab> tabs = (List<Tab>) event.getSource().getValue();
                    addTabs(tabs);
                    menuBar.updateRecentFiles();
                });
                task.runInNewThread();
            }
        } catch (Exception e) {
            Log.error(e);
            ViewerAlert.showExceptionAlert(e);
        }
    }

    public void addTabs(List<Tab> tabs) {
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
                        Log.error(e);
                        ViewerAlert.showExceptionAlert(e);
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

    public ViewerMenuBar getMenuBar() {
        return menuBar;
    }

    public ViewerTabPane getTabPane() {
        return tabPane;
    }
}
