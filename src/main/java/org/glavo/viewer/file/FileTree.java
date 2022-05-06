package org.glavo.viewer.file.tree;

import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.file.FileType;
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

    public static final class FolderNode extends FileTree {

        private String text;
        private int depth = 1;

        public FolderNode(FilePath path) {
            super(FolderType.TYPE, path);
        }

        public int getDepth() {
            return depth;
        }

        public void setDepth(int depth) {
            if (depth <= 0) {
                throw new IllegalArgumentException("illegal depth: " + depth);
            }
            if (depth > getPath().getPathElements().length) {
                throw new IllegalArgumentException(String.format("depth(%s) large than path length(%s)", depth, getPath().getPathElements().length));
            }

            this.depth = depth;
            this.text = null;
        }

        @Override
        public String getText() {
            if (text == null) {
                if (depth == 1) {
                    text = getPath().getFileName();
                } else {
                    String[] elements = getPath().getPathElements();
                    int beginIdx = elements.length - depth;

                    StringBuilder builder = new StringBuilder();
                    builder.append(elements[beginIdx++]);

                    while (beginIdx < elements.length) {
                        builder.append('/').append(elements[beginIdx++]);
                    }

                    text = builder.toString();
                }
            }
            return text;
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
