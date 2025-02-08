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
package org.glavo.viewer.file.types.classfile;

import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.glavo.viewer.ui.*;
import org.glavo.viewer.file.types.FileType;
import org.glavo.viewer.file.types.binary.HexText;
import org.glavo.viewer.util.ImageUtils;
import org.glavo.viewer.util.UrlUtils;

import java.net.URL;
import java.util.Objects;

public final class ClassFileType extends FileType {
    public static final ClassFileType Instance = new ClassFileType();

    private ClassFileType() {
        this.filter = new FileChooser.ExtensionFilter("Java Class File (*.class)", "*.class");
        this.icon = ImageUtils.loadImage("/icons/filetype/ClassFile.png");
    }

    @Override
    public boolean accept(URL url) {
        Objects.requireNonNull(url);
        return url.toString().toLowerCase().endsWith(".class");
    }

    @Override
    public ViewerTab open(Viewer viewer, URL url) {
        ViewerTab tab = ViewerTab.create(url);
        tab.setGraphic(new ImageView(icon));

        ViewerTask<Void> task = new ViewerTask<>() {
            @Override
            protected Void call() throws Exception {
                byte[] bytes = UrlUtils.readData(url);
                ClassFile classFile = new ClassFileParser().parse(bytes);
                HexText text = new HexText(bytes);
                Platform.runLater(() -> {
                    ParsedViewerPane pane = new ParsedViewerPane(viewer, classFile, text);
                    ((ClassFileComponent) pane.getTree().getRoot()).setName(UrlUtils.getClassName(url));
                    tab.setContent(pane);
                    tab.getUserData().showOrHideSearchBar = pane::showOrHideSearchBar;
                    RecentFile.addRecentFile(Instance, url);
                });
                return null;
            }
        };
        task.setOnFailed((Throwable e) -> {
            viewer.getTabPane().getTabs().remove(tab);
            ViewerAlert.logAndShowExceptionAlert(e);
        });

        task.startInNewThread();
        return tab;
    }

    @Override
    public String toString() {
        return "JAVA_CLASS";
    }
}
