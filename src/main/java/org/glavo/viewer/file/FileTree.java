package org.glavo.viewer.file;

import org.glavo.viewer.file.types.FolderType;

import java.util.ArrayList;
import java.util.List;

public abstract class FileTree {
    private final FileType type;
    private final FilePath path;
    private final List<FileTree> children = new ArrayList<>();

    public FileTree(FileType type, FilePath path) {
        this.type = type;
        this.path = path;
    }

    public abstract String getText();

    public FileType getType() {
        return type;
    }

    public FilePath getPath() {
        return path;
    }

    public List<FileTree> getChildren() {
        return children;
    }

    public static final class RootNode extends FileTree {

        public RootNode(FileType type, FilePath path) {
            super(type, path);
        }

        @Override
        public String getText() {
            return getPath().toString();
        }
    }

    public static class FolderNode extends FileTree {

        private final String name;

        public FolderNode(String name) {
            super(FolderType.TYPE, null);
            this.name = name;
        }

        @Override
        public String getText() {
            return name;
        }
    }

    public static final class MergedFolderNode extends FolderNode {
        public MergedFolderNode(String name) {
            super(name);
        }
    }

    public static final class FileNode extends FileTree {
        public FileNode(FileType type, FilePath path) {
            super(type, path);
        }

        @Override
        public String getText() {
            return getPath().getFileName();
        }
    }
}
