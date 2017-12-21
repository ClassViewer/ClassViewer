package org.glavo.viewer.gui;

import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import org.glavo.viewer.gui.support.ImageUtils;
import org.glavo.viewer.util.Log;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

public class MyToolBar extends ToolBar {
    private MyMenuBar menuBar;


    public MyToolBar(MyMenuBar menuBar) {
        this.menuBar = menuBar;

        addOpenFileBottom();
        getItems().add(new Separator());
        addOtherButtom();
    }

    public void addOpenFileBottom() {
        Button button = new Button(null, new ImageView(ImageUtils.openFileImage));
        button.setOnAction(e -> menuBar.onOpenFile.accept(null));
        button.setTooltip(new Tooltip("Open file"));
        this.getItems().add(button);
    }

    public void addOtherButtom() {
        Button button = new Button(null, new ImageView(ImageUtils.helpImage));
        button.setOnAction(e -> {
            try {
                Desktop.getDesktop().browse(URI.create("https://github.com/Glavo/ClassViewer/wiki"));
            } catch (IOException e1) {
                Log.log(e1);
            }
        });
        button.setTooltip(new Tooltip("Open user guide in browser"));
        this.getItems().add(button);
    }
}
