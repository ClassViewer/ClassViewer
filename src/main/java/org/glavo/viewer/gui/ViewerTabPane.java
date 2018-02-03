package org.glavo.viewer.gui;

import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.net.URL;

public final class ViewerTabPane extends TabPane {
    private Viewer viewer;

    public ViewerTabPane(Viewer viewer) {
        this.viewer = viewer;
        this.getSelectionModel().selectedItemProperty().addListener(this::onChange);
        this.getTabs().addListener((ListChangeListener<? super Tab>) c -> {
            if(this.getTabs().isEmpty()) {
                viewer.getStage().setTitle(Viewer.TITLE);
            }
        });
    }

    private void onChange(ObservableValue<? extends Tab> observable, Tab oldTab, Tab newTab) {
        if (newTab != null) {
            URL url = ((ViewerTab) newTab).getUserData().url;
            if (url == null) {
                viewer.getStage().setTitle(Viewer.TITLE);
            } else {
                viewer.getStage().setTitle(Viewer.TITLE + " - " + url);
            }
        }
    }
}
