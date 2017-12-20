package org.glavo.viewer.gui;

import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import org.glavo.viewer.gui.support.ImageUtils;

public class MyToolBar extends ToolBar {
    private MyMenuBar menuBar;


    public MyToolBar(MyMenuBar menuBar) {
        this.menuBar = menuBar;

        addOpenFileBottom();
    }

    public void addOpenFileBottom() {
        Button button = new Button(null, new ImageView(ImageUtils.openFileImage));
        button.setOnAction(e -> menuBar.onOpenFile.accept(null));
        button.setTooltip(new Tooltip("Open file"));
        this.getItems().add(button);
    }
}
