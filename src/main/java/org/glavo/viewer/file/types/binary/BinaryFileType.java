/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 Glavo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.glavo.viewer.file.types.binary;

import javafx.application.Platform;
import javafx.scene.image.ImageView;
import org.glavo.viewer.resources.Images;
import org.glavo.viewer.ui.*;
import org.glavo.viewer.file.types.FileType;
import org.glavo.viewer.util.UrlUtils;

import java.net.URL;

public class BinaryFileType extends FileType {
    public static final BinaryFileType Instance = new BinaryFileType();

    private BinaryFileType() {
        this.filter = null;
        this.icon = Images.ICON_UNKNOWN_FILE;
    }

    @Override
    public boolean accept(URL url) {
        return url != null && !url.toString().endsWith("/");
    }

    @Override
    public ViewerTab open(Viewer viewer, URL url) {
        ViewerTab tab = ViewerTab.create(url);
        tab.setGraphic(new ImageView(icon));

        ViewerTask<HexText> task = new ViewerTask<HexText>() {
            @Override
            protected HexText call() throws Exception {
                byte[] bytes = UrlUtils.readData(url);
                return new HexText(bytes);
            }
        };
        task.setOnSucceeded((HexText text) -> Platform.runLater(() -> {
            tab.setContent(new HexPane(text));
            RecentFile.addRecentFile(this, url);
        }));

        task.setOnFailed((Throwable ex) -> {
            viewer.getTabPane().getTabs().remove(tab);
            ViewerAlert.logAndShowExceptionAlert(ex);
        });

        task.startInNewThread();
        return tab;
    }

    @Override
    public String toString() {
        return "BINARY_FILE";
    }
}
