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
import kala.collection.immutable.primitive.ImmutableByteArray;
import kala.collection.primitive.ByteSeq;
import kala.function.CheckedSupplier;
import org.glavo.viewer.file.Container;
import org.glavo.viewer.file.CustomFileType;
import org.glavo.viewer.file.FileHandle;
import org.glavo.viewer.file.VirtualFile;
import org.glavo.viewer.resources.Images;
import org.glavo.viewer.ui.*;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.glavo.viewer.util.logging.Logger.LOGGER;

public class BinaryFileType extends CustomFileType {
    public static final BinaryFileType TYPE = new BinaryFileType();

    private BinaryFileType() {
        this("binary", Images.file, Set.of());
    }

    protected BinaryFileType(String name, Set<String> extensions) {
        super(name, extensions);
    }

    protected BinaryFileType(String name, Image image, Set<String> extensions) {
        super(name, image, extensions);
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
                    try {
                        fileHandle = container.openFile(file);
                        tab.setFileHandle(fileHandle);

                        ByteSeq bytes = ImmutableByteArray.Unsafe.wrap(fileHandle.readAllBytes());
                        if (bytes.size() < 200 * 1024) { // 200 KiB
                            return new ClassicHexPane(bytes);
                        } else {
                            return new FallbackHexPane(bytes);
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
                }), Schedulers.virtualThread())
                .whenCompleteAsync((result, exception) -> {
                    if (exception == null) {
                        tab.setContent(result.getNode());
                        tab.setStatusBar(result.getStatusBar());
                    } else {
                        LOGGER.warning("Failed to open file", exception);
                    }
                }, Schedulers.javafx());

        return tab;
    }
}
