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
package org.glavo.viewer.file.types.jmod;

import javafx.stage.FileChooser;
import org.glavo.viewer.file.types.jar.JarFileType;
import org.glavo.viewer.resources.Images;

import java.net.URL;

public final class JModFileType extends JarFileType {
    public static final JModFileType Instance = new JModFileType();

    private JModFileType() {
        super();
        this.icon = Images.ICON_ARCHIVE_FILE;
        this.filter = new FileChooser.ExtensionFilter("JMode File (*.jmod)", "*.jmod");
    }

    @Override
    public boolean accept(URL url) {
        String s = url.toString().toLowerCase();
        return s.endsWith(".jmod");
    }

    @Override
    public String toString() {
        return "JAVA_JMOD";
    }
}

