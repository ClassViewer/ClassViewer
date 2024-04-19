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
package org.glavo.viewer.file2.types;

import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import kala.collection.immutable.primitive.ImmutableByteArray;
import kala.collection.primitive.ByteSeq;
import kala.function.CheckedSupplier;
import org.glavo.viewer.file2.Container;
import org.glavo.viewer.file2.CustomFileType;
import org.glavo.viewer.file2.FileHandle;
import org.glavo.viewer.file2.VirtualFile;
import org.glavo.viewer.resources.Images;
import org.glavo.viewer.ui.*;
import org.glavo.viewer.util.FXUtils;

import java.lang.ref.WeakReference;
import java.util.concurrent.CompletableFuture;

import static org.glavo.viewer.util.logging.Logger.LOGGER;

public class BinaryFileType extends CustomFileType {
    public static final BinaryFileType TYPE = new BinaryFileType();

    private BinaryFileType() {
        super("binary", Images.file);
    }

    protected BinaryFileType(String name) {
        super(name);
    }

    protected BinaryFileType(String name, Image image) {
        super(name, image);
    }

    @Override
    public boolean check(VirtualFile path) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FileTab2 openTab(VirtualFile file) {
        var tab = new FileTab2(file, this);
        var indicator = new ProgressIndicator();

        tab.setContent(new StackPane(indicator));

        CompletableFuture.supplyAsync(CheckedSupplier.of(() -> {
                    Container container = file.getContainer();
                    container.lock();
                    try {
                        FileHandle fileHandle = container.openFile(file);

                        tab.setFileHandle(fileHandle);

                        WeakReference<FileTab2> tabRef = new WeakReference<>(tab);
                        fileHandle.setOnForceClose(() -> FXUtils.runInFx(() -> {
                            tab.setFileHandle(null);
                            tab.getTabPane().getTabs().remove(tab);
                        }));

                        ByteSeq bytes = ImmutableByteArray.Unsafe.wrap(fileHandle.readAllBytes());
                        if (bytes.size() < 200 * 1024) { // 200 KiB
                            return new ClassicHexPane(bytes);
                        } else {
                            return new FallbackHexPane(bytes);
                        }
                    } finally {
                        container.unlock();
                    }
                }), Schedulers.virtualThread())
                .whenCompleteAsync((result, exception) -> {
                    if (exception == null) {
                        tab.setOnClosed(event -> {
                            FileHandle fileHandle = tab.getFileHandle();
                            if (fileHandle != null) {
                                fileHandle.close();
                            }
                        });
                    } else {
                        LOGGER.warning("", exception);
                    }
                }, Schedulers.javafx());

        return tab;
    }
}
