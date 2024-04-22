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

import javafx.scene.Node;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import kala.collection.immutable.primitive.ImmutableByteArray;
import kala.function.CheckedSupplier;
import kala.tuple.Tuple2;
import org.glavo.viewer.file.Container;
import org.glavo.viewer.file.CustomFileType;
import org.glavo.viewer.file.FileHandle;
import org.glavo.viewer.file.VirtualFile;
import org.glavo.viewer.ui.*;

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

    protected Node openSideBar(FileTab tab, HexPane hexPane, byte[] bytes) throws Throwable {
        return null;
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
                    byte[] bytes;
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

                    HexPane hexPane;
                    if (bytes.length < 200 * 1024) { // 200 KiB
                        hexPane = new ClassicHexPane(ImmutableByteArray.Unsafe.wrap(bytes));
                    } else {
                        hexPane = new FallbackHexPane(ImmutableByteArray.Unsafe.wrap(bytes));
                    }

                    Node sideBar;

                    try {
                        sideBar = openSideBar(tab, hexPane, bytes);
                    } catch (Throwable e) {
                        LOGGER.warning("Failed to open side bar", e);
                        sideBar = null;
                    }

                    return new Tuple2<>(hexPane, sideBar);
                }), Schedulers.virtualThread())
                .whenCompleteAsync((result, exception) -> {
                    if (exception == null) {
                        tab.setContent(result.component1().getNode());
                        tab.setStatusBar(result.component1().getStatusBar());
                        tab.setSideBar(result.component2());
                    } else {
                        LOGGER.warning("Failed to open file", exception);
                    }
                }, Schedulers.javafx());

        return tab;
    }
}
