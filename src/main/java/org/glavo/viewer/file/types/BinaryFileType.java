package org.glavo.viewer.file.types;

import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.glavo.viewer.file.FileHandle;
import org.glavo.viewer.file.LocalFilePath;
import org.glavo.viewer.resources.I18N;
import org.glavo.viewer.resources.Images;
import org.glavo.viewer.ui.*;
import org.glavo.viewer.util.ByteArrayList;
import org.glavo.viewer.util.ByteList;
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
    public boolean check(LocalFilePath path) {
        throw new UnsupportedOperationException();
    }

    protected void openContent(FileTab tab, FileHandle handle, ByteList bytes) {
    }

    @Override
    public FileTab openTab(FileHandle handle) {
        FileTab res = new FileTab(this, handle.getPath());
        res.setContent(new StackPane(new ProgressIndicator()));

        TaskUtils.submit(new Task<ByteList>() {
            @Override
            protected ByteList call() throws Exception {
                return ByteArrayList.wrap(handle.readAllBytes());
            }

            @Override
            protected void succeeded() {
                ByteList bytes = getValue();

                TaskUtils.submit(new Task<HexPane>() {
                    @Override
                    protected HexPane call() throws Exception {
                        if (bytes.size() < 200 * 1024) { // 200 KiB
                            return new ClassicHexPane(bytes);
                        } else {
                            return new FallbackHexPane(bytes);
                        }
                    }

                    @Override
                    protected void succeeded() {
                        res.setContent(getValue().getNode());

                        // StatusBar (TODO?)
                        BorderPane statusBar = new BorderPane();

                        Label statusLabel = new Label(" ");
                        statusBar.setLeft(statusLabel);

                        HexPane.BytesBar bytesBar = new HexPane.BytesBar(bytes.size());
                        bytesBar.setMaxHeight(statusLabel.getMaxHeight());
                        bytesBar.setPrefWidth(200);
                        statusBar.setRight(bytesBar);

                        res.setStatusBar(statusBar);
                        getValue().setOnSelect((tuple) -> bytesBar.select(tuple._1, tuple._2));
                    }

                    @Override
                    protected void failed() {
                        LOGGER.log(Level.WARNING, "Failed to create hex pane", getException());
                        res.setContent(new EmptyHexPane(bytes));
                    }
                });

                openContent(res, handle, bytes);
            }

            @Override
            protected void failed() {
                LOGGER.log(Level.WARNING, "Failed to read values", getException());
                res.setContent(new StackPane(new Label(I18N.getString("failed.openFile"))));
                handle.close();
            }
        });


        return res;
    }
}
