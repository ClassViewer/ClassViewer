package org.glavo.viewer.ui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.file.FileType;

public class FileTab extends Tab {
    private final FileType type;
    private final FilePath path;

    private final ObjectProperty<Node> sideBar = new SimpleObjectProperty<>();

    public FileTab(FileType type, FilePath path) {
        this.type = type;
        this.path = path;

        this.setGraphic(new ImageView(type.getImage()));
        this.setText(path.getFileName());
        this.setTooltip(new Tooltip(path.toString()));
    }

    public FileType getType() {
        return type;
    }

    public FilePath getPath() {
        return path;
    }

    public ObjectProperty<Node> sideBarProperty() {
        return sideBar;
    }

    public Node getSideBar() {
        return sideBar.get();
    }

    public void setSideBar(Node sideBar) {
        this.sideBar.set(sideBar);
    }
}
