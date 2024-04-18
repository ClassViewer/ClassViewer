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

import javafx.scene.image.Image;
import org.glavo.viewer.file2.CustomFileType;
import org.glavo.viewer.file2.FileHandle;
import org.glavo.viewer.file2.VirtualFile;
import org.glavo.viewer.resources.Images;
import org.glavo.viewer.ui.*;

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
    public FileTab2 openTab(FileHandle handle) {
        throw new UnsupportedOperationException(); // TODO
    }
}
