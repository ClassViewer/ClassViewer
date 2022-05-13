package org.glavo.viewer.file.types;

import javafx.concurrent.Task;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.glavo.viewer.file.FileHandle;
import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.ui.FileTab;
import org.glavo.viewer.util.TaskUtils;

import java.io.ByteArrayInputStream;

public class ImageFileType extends CustomFileType {

    public static final ImageFileType TYPE = new ImageFileType();

    private ImageFileType() {
        super("image");
    }

    @Override
    public boolean check(FilePath path) {
        switch (path.getFileNameExtension()) {
            case "bmp":
            case "gif":
            case "png":
            case "jpg":
            case "jpeg":
            case "webp":
            case "ico":
                return true;
        }

        return false;
    }

    @Override
    public FileTab openTab(FileHandle handle) {
        FileTab res = new FileTab(this, handle.getPath());
        res.setContent(new ProgressIndicator());

        Task<ImageView> task = new Task<ImageView>() {
            @Override
            protected ImageView call() throws Exception {
                byte[] bytes = handle.readAllBytes();

                return new ImageView(new Image(new ByteArrayInputStream(bytes)));
            }

            @Override
            protected void succeeded() {
                ImageView view = getValue();
                res.setContent(new ScrollPane(view));

                handle.close();
            }

            @Override
            protected void failed() {
                handle.close();
                throw new UnsupportedOperationException(getException()); // TODO
            }
        };

        TaskUtils.submit(task);
        return res;
    }
}
