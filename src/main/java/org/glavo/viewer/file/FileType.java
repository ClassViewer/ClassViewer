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
package org.glavo.viewer.file;

import javafx.scene.image.Image;
import org.glavo.viewer.file.types.aya.AyaSourceFileType;
import org.glavo.viewer.file.types.css.CSSFileType;
import org.glavo.viewer.file.types.html.HTMLFileType;
import org.glavo.viewer.file.types.image.ImageFileType;
import org.glavo.viewer.file.types.java.*;
import org.glavo.viewer.file.types.json.JsonFileType;
import org.glavo.viewer.file.types.plain.PlainTextFileType;
import org.glavo.viewer.file.types.raw.RawBinaryFileType;
import org.glavo.viewer.file.types.xml.XMLFileType;
import org.glavo.viewer.file.types.yaml.YAMLFileType;
import org.glavo.viewer.resources.Images;

import java.util.List;
import java.util.Set;

public abstract sealed class FileType permits ContainerFileType, CustomFileType, DirectoryFileType {
    private final String name;
    private final Image image;
    private final Set<String> extensions;

    protected FileType(String name) {
        this(name, Set.of());
    }

    protected FileType(String name, Set<String> extensions) {
        this(name, Images.loadImage("fileTypes/file-" + name + ".png"), extensions);
    }

    protected FileType(String name, Image image, Set<String> extensions) {
        this.name = name;
        this.image = image;
        this.extensions = extensions;
    }

    public String getName() {
        return name;
    }

    public Image getImage() {
        return image;
    }

    public boolean check(VirtualFile file, String ext) {
        return extensions.contains(ext);
    }

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
        if (file.isDirectory()) {
            return DirectoryFileType.TYPE;
        }

        String fileName = file.getFileName();
        int idx = fileName.lastIndexOf('.');
        String extension = idx > 0 ? fileName.substring(idx + 1) : "";

        for (FileType extType : Holder.extTypes) {
            if (extType.check(file, extension)) {
                return extType;
            }
        }

        return PlainTextFileType.TYPE.check(file, extension) ? PlainTextFileType.TYPE : RawBinaryFileType.TYPE;
    }

    private static final class Holder {
        static final List<FileType> extTypes = List.of(
                // JImageFileType.TYPE,
                // ArchiveFileType.TYPE,
                // TarFileType.TYPE,

                ManifestFileType.TYPE,
                PropertiesFileType.TYPE,
                XMLFileType.TYPE,
                YAMLFileType.TYPE,
                CSSFileType.TYPE,
                HTMLFileType.TYPE,
                JsonFileType.TYPE,
                AyaSourceFileType.TYPE,
                JavaSourceFileType.TYPE,
                JavaModuleInfoFileType.TYPE,
                // MarkdownFileType.TYPE,

                JavaClassFileType.TYPE,

                ImageFileType.TYPE
        );
    }
}
