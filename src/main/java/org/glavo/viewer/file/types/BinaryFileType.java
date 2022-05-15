package org.glavo.viewer.file.types;

import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import org.glavo.viewer.file.FileHandle;
import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.resources.I18N;
import org.glavo.viewer.resources.Images;
import org.glavo.viewer.ui.FileTab;
import org.glavo.viewer.ui.ClassicHexPane;
import org.glavo.viewer.ui.HexPane;
import org.glavo.viewer.ui.ModernHexPane;
import org.glavo.viewer.util.TaskUtils;

import java.util.logging.Level;

import static org.glavo.viewer.util.Logging.LOGGER;

public class BinaryFileType extends CustomFileType {
    public static final BinaryFileType TYPE = new BinaryFileType();

    public BinaryFileType() {
        super("binary", Images.file);
    }

    protected BinaryFileType(String name) {
        super(name);
    }

    protected BinaryFileType(String name, Image image) {
        super(name, image);
    }

    @Override
    public boolean check(FilePath path) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FileTab openTab(FileHandle handle) {
        FileTab res = new FileTab(this, handle.getPath());
        res.setContent(new StackPane(new ProgressIndicator()));

        Task<HexPane> task = new Task<HexPane>() {
            @Override
            protected HexPane call() throws Exception {
                return new ModernHexPane(handle.readAllBytes());
            }

            @Override
            protected void succeeded() {
                res.setContent(this.getValue().getNode());
                handle.close();
            }

            @Override
            protected void failed() {
                LOGGER.log(Level.WARNING, "Failed to open file", getException());
                res.setContent(new StackPane(new Label(I18N.getString("failed.openFile"))));
                handle.close();
            }
        };

        TaskUtils.submit(task);

        return res;
    }
}
