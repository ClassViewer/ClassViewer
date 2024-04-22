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

import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import kala.function.CheckedSupplier;
import org.glavo.viewer.file.Container;
import org.glavo.viewer.file.CustomFileType;
import org.glavo.viewer.file.FileHandle;
import org.glavo.viewer.file.VirtualFile;
import org.glavo.viewer.resources.I18N;
import org.glavo.viewer.ui.FileTab;
import org.glavo.viewer.ui.Schedulers;

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
        tab.setContent(new StackPane(new ProgressIndicator()));

        CompletableFuture.supplyAsync(CheckedSupplier.of(() -> {
            Container container = file.getContainer();
            container.lock();
            FileHandle fileHandle = null;
            Image image;
            try {
                fileHandle = container.openFile(file);
                tab.setFileHandle(fileHandle);
                try (InputStream inputStream = fileHandle.openInputStream()) {
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
        }), Schedulers.virtualThread()).whenCompleteAsync((result, exception) -> {
            if (exception == null) {
                tab.setContent(new ImageView(result));
            } else {
                LOGGER.warning("Failed to read image file " + file, exception);
                tab.setContent(new StackPane(new Label(I18N.getString("file.wrongFormat"))));
            }
        }, Schedulers.javafx());

        return tab;
    }
}
