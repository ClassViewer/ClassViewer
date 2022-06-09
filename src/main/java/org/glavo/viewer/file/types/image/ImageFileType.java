package org.glavo.viewer.file.types.image;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.concurrent.Task;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Pair;
import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.ImageInfo;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.glavo.viewer.file.FileHandle;
import org.glavo.viewer.file.OldFilePath;
import org.glavo.viewer.file.types.CustomFileType;
import org.glavo.viewer.resources.I18N;
import org.glavo.viewer.ui.FileTab;
import org.glavo.viewer.util.TaskUtils;

import java.io.ByteArrayInputStream;
import java.util.logging.Level;

import static org.glavo.viewer.util.Logging.LOGGER;

public class ImageFileType extends CustomFileType {

    public static final ImageFileType TYPE = new ImageFileType();

    private ImageFileType() {
        super("image");
    }

    @Override
    public boolean check(OldFilePath path) {
        switch (path.getFileNameExtension()) {
            case "bmp":
            case "gif":
            case "png":
            case "jpg":
            case "jpeg":
            case "webp":
            case "ico":
            case "cur":
                return true;
        }

        return false;
    }

    @Override
    public FileTab openTab(FileHandle handle) {
        FileTab res = new FileTab(this, handle.getPath());
        res.setContent(new StackPane(new ProgressIndicator()));
        res.setSideBar(new StackPane(new ProgressIndicator()));

        Task<ImageView> task = new Task<ImageView>() {
            ImageInfoTable infoTable;

            @Override
            protected ImageView call() throws Exception {
                byte[] bytes;
                try {
                    bytes = handle.readAllBytes();
                } finally {
                    handle.close();
                }

                try {
                    infoTable = new ImageInfoTable(Imaging.getImageInfo(bytes));
                } catch (Throwable e) {
                    LOGGER.log(Level.WARNING, "Failed to read image information", e);
                }
                Image image = new Image(new ByteArrayInputStream(bytes));
                if (image.isError()) {
                    throw new ImageReadException("Failed to read image");
                }

                return new ImageView(image);
            }

            @Override
            protected void succeeded() {
                ImageView view = getValue();
                res.setContent(new ScrollPane(view));
                res.setSideBar(infoTable);
                handle.close();
            }

            @Override
            protected void failed() {
                LOGGER.log(Level.WARNING, "Failed to read image", getException());
                if (getException() instanceof ImageReadException) {
                    res.setContent(new StackPane(new Label(I18N.getString("image.unsupported"))));
                } else {
                    res.setContent(new StackPane(new Label(I18N.getString("failed.openFile"))));
                }
                res.setSideBar(infoTable);
            }
        };

        TaskUtils.submit(task);
        return res;
    }

    private static final class ImageInfoTable extends TableView<Pair<String, String>> {
        ImageInfoTable(ImageInfo info) {
            this.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            TableColumn<Pair<String, String>, String> attributeName = new TableColumn<>(I18N.getString("attribute.name"));
            TableColumn<Pair<String, String>, String> attributeValue = new TableColumn<>(I18N.getString("attribute.value"));

            attributeName.setCellValueFactory(f -> new ReadOnlyStringWrapper(f.getValue().getKey()));
            attributeValue.setCellValueFactory(f -> new ReadOnlyStringWrapper(f.getValue().getValue()));

            getColumns().add(attributeName);
            getColumns().add(attributeValue);

            getItems().add(new Pair<>(I18N.getString("image.attributes.format"), info.getFormat().getName()));
            getItems().add(new Pair<>(I18N.getString("image.attributes.width"), String.valueOf(info.getWidth())));
            getItems().add(new Pair<>(I18N.getString("image.attributes.height"), String.valueOf(info.getHeight())));
            getItems().add(new Pair<>(I18N.getString("image.attributes.bitsPerPixel"), String.valueOf(info.getBitsPerPixel())));
            getItems().add(new Pair<>(I18N.getString("image.attributes.colorType"), info.getColorType().toString()));
            getItems().add(new Pair<>(I18N.getString("image.attributes.compressionAlgorithm"), info.getCompressionAlgorithm().toString()));

            if (info.getFormat() == ImageFormats.GIF || info.getFormat() == ImageFormats.TIFF) {
                getItems().add(new Pair<>(I18N.getString("image.attributes.numberOfImages"), String.valueOf(info.getNumberOfImages())));
            }

            if (info.getFormat() == ImageFormats.TIFF
                    || info.getFormat() == ImageFormats.BMP
                    || info.getFormat() == ImageFormats.JPEG
                    || info.getFormat() == ImageFormats.PNG) {
                if (info.getPhysicalWidthInch() >= 0)
                    getItems().add(new Pair<>(I18N.getString("image.attributes.physicalWidthInch"), String.valueOf(info.getPhysicalWidthInch())));
                if (info.getPhysicalHeightInch() >= 0)
                    getItems().add(new Pair<>(I18N.getString("image.attributes.physicalHeightInch"), String.valueOf(info.getPhysicalHeightInch())));

                if (info.getPhysicalWidthDpi() >= 0)
                    getItems().add(new Pair<>(I18N.getString("image.attributes.physicalWidthDpi"), String.valueOf(info.getPhysicalWidthDpi())));
                if (info.getPhysicalHeightDpi() >= 0)
                    getItems().add(new Pair<>(I18N.getString("image.attributes.physicalHeightDpi"), String.valueOf(info.getPhysicalHeightDpi())));
            }
        }
    }
}
