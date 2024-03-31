package org.glavo.viewer.file;

import javafx.collections.ObservableList;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import org.glavo.viewer.file.types.folder.FolderType;
import org.glavo.viewer.resources.Images;

import java.util.logging.Level;

import static org.glavo.viewer.util.Logging.LOGGER;

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

    private ContainerHandle containerHandle;
    private boolean needToInit = getType() instanceof ContainerFileType;

    public void setContainerHandle(ContainerHandle containerHandle) {
        this.containerHandle = containerHandle;
    }

    public ContainerHandle getContainerHandle() {
        return containerHandle;
    }

    @Override
    public ObservableList<TreeItem<String>> getChildren() {
        ObservableList<TreeItem<String>> children = super.getChildren();
        if (needToInit) {
            needToInit = false;

            if (getType() instanceof FolderType) {
                // TODO
            } else if (getType() instanceof ContainerFileType t) {
                try {
                    LOGGER.info("Expand " + getPath());
                    Container container = Container.getContainer(getPath());
                    setContainerHandle(new ContainerHandle(container));
                    // OldFileTree.buildFileTree(container, node);
                } catch (Throwable e) {
                    LOGGER.log(Level.WARNING, "Failed to open container", e);
                }
            } else {
                throw new AssertionError();
            }
        }

        return children;
    }

    @Override
    public boolean isLeaf() {
        return !needToInit && super.getChildren().isEmpty();
    }

    public enum Status {
        DEFAULT,
        FAILED,
        LOADING,
        UNEXPANDED
    }
}
