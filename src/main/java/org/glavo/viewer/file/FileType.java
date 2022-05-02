package org.glavo.viewer.file;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import javafx.scene.image.Image;
import org.glavo.viewer.file.types.*;
import org.glavo.viewer.resources.Images;

import java.util.*;

public abstract class FileType {
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
            return Hole.FOLDER_TYPE;
        }

        for (FileType type : Hole.extTypes) {
            if (type.check(path)) {
                return type;
            }
        }

        return Hole.BINARY_FILE_TYPE;
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
        static final BinaryFileType BINARY_FILE_TYPE = new BinaryFileType();
        static final FolderType FOLDER_TYPE = new FolderType();
        static final TextFileType TEXT_FILE_TYPE = new TextFileType();


        @SuppressWarnings("Java9CollectionFactory")
        static final List<FileType> extTypes = Collections.unmodifiableList(Arrays.asList(
                new JImageFileType(),
                new ArchiveFileType(),

                new ManifestFileType(),
                new PropertiesFileType(),

                new JavaClassFileType()
        ));

        static final List<FileType> types;

        static {
            ArrayList<FileType> list = new ArrayList<>(extTypes);
            list.add(BINARY_FILE_TYPE);
            list.add(FOLDER_TYPE);
            list.add(TEXT_FILE_TYPE);

            types = Collections.unmodifiableList(list);
        }
    }
}
