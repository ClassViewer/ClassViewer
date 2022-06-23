package org.glavo.viewer.file;

import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import org.glavo.viewer.resources.Images;

public final class FileTree extends TreeItem<String> {
    private final FileType type;
    private final FilePath path;

    private Status status;

    public FileTree(FileType type, FilePath path) {
        this.type = type;
        this.path = path;
    }

    public FileType getType() {
        return type;
    }

    public FilePath getPath() {
        return path;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        if (status != this.status) {
            this.status = status;

            switch (status) {
                case DEFAULT, UNEXPANDED -> this.setGraphic(new ImageView(type.getImage()));
                case FAILED -> this.setGraphic(new ImageView(Images.failed));
                case LOADING -> {
                    ProgressIndicator indicator = new ProgressIndicator();
                    indicator.setPrefSize(16, 16);
                    this.setGraphic(indicator);
                }
            }
        }
    }

    public enum Status {
        DEFAULT,
        FAILED,
        LOADING,
        UNEXPANDED
    }
}
