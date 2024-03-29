package org.glavo.viewer.file;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import javafx.scene.image.Image;
import org.glavo.viewer.file.types.*;
import org.glavo.viewer.file.types.aya.AyaSourceFileType;
import org.glavo.viewer.file.types.css.CSSFileType;
import org.glavo.viewer.file.types.folder.FolderType;
import org.glavo.viewer.file.types.html.HTMLFileType;
import org.glavo.viewer.file.types.image.ImageFileType;
import org.glavo.viewer.file.types.java.*;
import org.glavo.viewer.file.types.json.JsonFileType;
import org.glavo.viewer.file.types.markdown.MarkdownFileType;
import org.glavo.viewer.file.types.tar.TarFileType;
import org.glavo.viewer.file.types.xml.XMLFileType;
import org.glavo.viewer.file.types.yaml.YAMLFileType;
import org.glavo.viewer.file.types.zip.ArchiveFileType;
import org.glavo.viewer.resources.Images;

import java.util.*;

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

    public boolean isContainer() {
        return this instanceof ContainerFileType;
    }

    @JsonValue
    public String getName() {
        return name;
    }

    public Image getImage() {
        return image;
    }

    public abstract boolean check(FilePath path);

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

    public static List<FileType> getTypes() {
        return Hole.types;
    }

    public static FileType detectFileType(FilePath path) {
        if (path.isDirectory()) {
            return FolderType.TYPE;
        }

        for (FileType type : Hole.extTypes) {
            if (type.check(path)) {
                return type;
            }
        }

        return TextFileType.TYPE.check(path) ? TextFileType.TYPE : BinaryFileType.TYPE;

    }

    @JsonCreator
    public static FileType ofName(String name) {
        for (FileType type : getTypes()) {
            if (name.equals(type.name)) {
                return type;
            }
        }

        return new UnknownFileType(name);
    }

    private static final class Hole {
        static final List<FileType> extTypes = Collections.unmodifiableList(Arrays.asList(
                JImageFileType.TYPE,
                ArchiveFileType.TYPE,
                TarFileType.TYPE,

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
                MarkdownFileType.TYPE,

                JavaClassFileType.TYPE,

                ImageFileType.TYPE
        ));

        static final List<FileType> types;

        static {
            ArrayList<FileType> list = new ArrayList<>(extTypes);
            list.add(BinaryFileType.TYPE);
            list.add(FolderType.TYPE);
            list.add(TextFileType.TYPE);

            types = Collections.unmodifiableList(list);
        }
    }
}
