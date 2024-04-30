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
package org.glavo.viewer.file.types;

import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import kala.function.CheckedSupplier;
import org.glavo.viewer.annotation.FXThread;
import org.glavo.viewer.file.Container;
import org.glavo.viewer.file.CustomFileType;
import org.glavo.viewer.file.FileHandle;
import org.glavo.viewer.file.VirtualFile;
import org.glavo.viewer.resources.I18N;
import org.glavo.viewer.ui.*;
import org.glavo.viewer.util.FXUtils;
import org.glavo.viewer.util.Schedulers;

import java.lang.foreign.MemorySegment;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.glavo.viewer.util.logging.Logger.LOGGER;

public abstract class BinaryFileType extends CustomFileType {

    protected BinaryFileType(String name, Set<String> extensions) {
        super(name, extensions);
    }

    protected BinaryFileType(String name, Image image, Set<String> extensions) {
        super(name, image, extensions);
    }

    @FXThread
    protected void parseContent(BinaryPane binaryPane) {
    }

    @Override
    public FileTab openTab(VirtualFile file) {
        var tab = new FileTab(file, this);
        var indicator = new ProgressIndicator();

        tab.setContent(new StackPane(indicator));

        CompletableFuture.supplyAsync(CheckedSupplier.of(() -> {
            Container container = file.getContainer();
            container.lock();
            FileHandle fileHandle = null;
            MemorySegment bytes;
            try {
                fileHandle = container.openFile(file);
                tab.setFileHandle(fileHandle);
                bytes = fileHandle.readAllBytes();
            } catch (Throwable e) {
                tab.setFileHandle(null);
                if (fileHandle != null) {
                    fileHandle.close();
                }
                throw e;
            } finally {
                container.unlock();
            }

            return bytes;
        }), Schedulers.io()).whenCompleteAsync((bytes, exception) -> {
            if (exception == null) {
                BinaryPane binaryPane = new BinaryPane(tab, bytes);
                parseContent(binaryPane);
                if (binaryPane.getView() == null) {
                    binaryPane.setView(BinaryPane.View.BINARY);
                }
            } else {
                LOGGER.warning("Failed to open file", exception);
                tab.setContent(new StackPane(FXUtils.exceptionDialogLink(I18N.getString("failed.openFile"), exception)));
            }
        }, Schedulers.javafx());

        return tab;
    }
}
