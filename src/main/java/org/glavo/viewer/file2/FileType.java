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
package org.glavo.viewer.file2;

import javafx.scene.image.Image;
import org.glavo.viewer.resources.Images;

public abstract sealed class FileType permits ContainerFileType, CustomFileType {
    private final String name;
    private final Image image;

    protected FileType(String name) {
        this(name, Images.loadImage("fileTypes/file-" + name + ".png"));
    }

    protected FileType(String name, Image image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public Image getImage() {
        return image;
    }

    public abstract boolean check(VirtualFile file);

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof FileType)) {
            return false;
        }

        return this.name.equals(((FileType) obj).name);
    }

    @Override
    public String toString() {
        return name;
    }

    public static FileType detectFileType(VirtualFile file) {
        return null; // TODO
    }

    public static FileType ofName(String name) {
        throw new UnsupportedOperationException(); // TODO
    }
}
