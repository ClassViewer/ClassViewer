package org.glavo.viewer.file;

import org.glavo.viewer.file.types.FolderType;

import java.util.List;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

public abstract class FileTree implements Comparable<FileTree> {
    private final FileType type;
    private final FilePath path;
    private final NavigableSet<FileTree> children = new TreeSet<>();

    FileTree(FileType type, FilePath path) {
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

    public Set<FileTree> getChildren() {
        return children;
    }

    public static void buildFileTree(Container container, FileTree root) throws Exception {
        NavigableSet<FilePath> files = container.resolveFiles();

        // build tree
        for (FilePath file : files) {
            assert file.getParent() == root.getPath();

            String[] elements = file.getPathElements();

            FileTree node = root;

            outer:
            for (int i = 0; i < elements.length - 1; i++) {
                String element = elements[i];

                inner:
                for (FileTree child : node.getChildren()) {
                    if (child instanceof FolderNode) {
                        if (((FolderNode) child).getTopName().equals(element)) {
                            node = child;
                            continue outer;
                        }
                    } else {
                        //noinspection UnnecessaryLabelOnBreakStatement
                        break inner;
                    }
                }

                FolderNode newNode = new FolderNode(element);
                node.getChildren().add(newNode);
                node = newNode;
            }

            node.getChildren().add(new FileNode(FileType.detectFileType(file), file));
        }

        // merge folder nodes
        // TODO
    }

    @Override
    public int compareTo(FileTree other) {
        if (this instanceof RootNode || other instanceof RootNode) {
            throw new UnsupportedOperationException("RootNode::compareTo");
        }

        if (this instanceof FileNode && other instanceof FileNode) {
            return this.getPath().getFileName().compareTo(other.getPath().getFileName());
        }

        if (this instanceof FolderNode && other instanceof FolderNode) {
            return ((FolderNode) this).getTopName().compareTo(((FolderNode) other).getTopName());
        }

        return this instanceof FolderNode ? -1 : 1;
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

        protected final String name;

        public FolderNode(String name) {
            super(FolderType.TYPE, null);
            this.name = name;
        }

        public String getTopName() {
            return name;
        }

        @Override
        public String getText() {
            return name;
        }
    }

    public static final class MergedFolderNode extends FolderNode {
        private String topName;

        public MergedFolderNode(String name) {
            super(name);
        }

        @Override
        public String getTopName() {
            if (topName == null) {
                int idx = name.indexOf('/');
                topName = idx < 0 ? name : name.substring(0, idx);
            }
            return super.getTopName();
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
