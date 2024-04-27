/*
 * Copyright 2024 Glavo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.glavo.viewer.file.types.image;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Pair;
import kala.function.CheckedSupplier;
import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.ImageInfo;
import org.glavo.viewer.file.Container;
import org.glavo.viewer.file.CustomFileType;
import org.glavo.viewer.file.FileHandle;
import org.glavo.viewer.file.VirtualFile;
import org.glavo.viewer.resources.I18N;
import org.glavo.viewer.ui.FileTab;
import org.glavo.viewer.util.Schedulers;

import java.io.InputStream;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.glavo.viewer.util.logging.Logger.LOGGER;

public final class ImageFileType extends CustomFileType {
    public static final ImageFileType TYPE = new ImageFileType();

    private ImageFileType() {
        super("image", Set.of("jpg", "jpeg", "png", "gif", "bmp"));
    }

    @Override
    public FileTab openTab(VirtualFile file) {
        FileTab tab = new FileTab(file, this);
        StackPane mainPane = new StackPane(new ProgressIndicator());
        tab.setContent(mainPane);

        CompletableFuture.supplyAsync(CheckedSupplier.of(() -> {
            Container container = file.getContainer();
            container.lock();
            FileHandle fileHandle = null;
            Image image;
            try {
                fileHandle = container.openFile(file);
                tab.setFileHandle(fileHandle);
                try (InputStream inputStream = fileHandle.getInputStream()) {
                    image = new Image(inputStream);
                }
            } catch (Throwable e) {
                tab.setFileHandle(null);
                if (fileHandle != null) {
                    fileHandle.close();
                }
                throw e;
            } finally {
                container.unlock();
            }

            return image;
        }), Schedulers.io()).whenCompleteAsync((result, exception) -> {
            if (exception == null) {
                mainPane.getChildren().setAll(new ImageView(result));
            } else {
                LOGGER.warning("Failed to read image file " + file, exception);
                mainPane.getChildren().setAll(new Label(I18N.getString("file.wrongFormat")));
            }
        }, Schedulers.javafx());

        return tab;
    }

    private static final class ImageInfoTable extends TableView<Pair<String, String>> {
        ImageInfoTable(ImageInfo info) {
            this.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
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
