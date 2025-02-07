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
package org.glavo.viewer.file.types;

import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import org.glavo.viewer.ui.Viewer;
import org.glavo.viewer.ui.ViewerTab;
import org.glavo.viewer.file.types.binary.BinaryFileType;
import org.glavo.viewer.file.types.classfile.ClassFileType;
import org.glavo.viewer.file.types.jar.JarFileType;
import org.glavo.viewer.file.types.jmod.JModFileType;

import java.net.URL;

public abstract class FileType {
    public static final FileType[] fileTypes = {
            ClassFileType.Instance,
            JarFileType.Instance,
            JModFileType.Instance,
            BinaryFileType.Instance,
            FolderType.Instance
    };

    public static final FileChooser.ExtensionFilter allFiles = new FileChooser.ExtensionFilter(
            "All files", "*.*"
    );

    public static FileType valueOf(String name) {
        for (FileType fileType : fileTypes) {
            if (fileType.toString().equals(name)) {
                return fileType;
            }
        }
        throw new IllegalArgumentException("No Such FileType: " + name);
    }

    public static FileType typeOf(URL url) {
        for (FileType type : fileTypes) {
            if (type.accept(url)) {
                return type;
            }
        }
        return null;
    }

    public FileChooser.ExtensionFilter filter = null;

    public Image icon = null;

    public abstract boolean accept(URL url);

    public abstract ViewerTab open(Viewer viewer, URL url) throws Exception;

    @Override
    public abstract String toString();
}
