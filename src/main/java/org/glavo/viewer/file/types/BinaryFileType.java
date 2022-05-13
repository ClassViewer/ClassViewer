package org.glavo.viewer.file.types;

import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.file.FileStub;
import org.glavo.viewer.file.FileType;
import org.glavo.viewer.resources.Images;
import org.glavo.viewer.ui.FileTab;
import org.glavo.viewer.ui.HexPane;
import org.glavo.viewer.util.HexText;
import org.glavo.viewer.util.TaskUtils;

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
    public FileTab openTab(FileStub stub) {
        FileTab res = new FileTab(this, stub.getPath());
        res.setContent(new StackPane(new ProgressIndicator()));

        Task<Node> task = new Task<Node>() {
            @Override
            protected Node call() throws Exception {
                return new HexPane(new HexText(stub.readAllBytes()));
            }

            @Override
            protected void succeeded() {
                res.setContent(this.getValue());
            }

            @Override
            protected void failed() {
                throw new UnsupportedOperationException(); // TODO
            }
        };

        TaskUtils.submit(task);

        return res;
    }
}
