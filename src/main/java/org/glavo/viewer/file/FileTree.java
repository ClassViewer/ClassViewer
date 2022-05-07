package org.glavo.viewer.file;

import org.glavo.viewer.file.types.FolderType;

import java.util.*;

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
            String[] elements = root.getPath().relativize(file);

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
        Queue<FileTree> queue = new ArrayDeque<>();
        addAllFolderNode(queue, root.getChildren());

        while (!queue.isEmpty()) {
            FolderNode node = (FolderNode) queue.remove();

            FileTree n = node;
            FileTree last = null;
            while (n.getChildren().size() == 1) {
                last = n.getChildren().iterator().next();
                if (!(last instanceof FolderNode)) {
                    break;
                }
                node.appendName(((FolderNode) last).getTopName());

                n = last;
            }

            if (last == null) {
                addAllFolderNode(queue, node.getChildren());
            } else if (last == n) {
                node.getChildren().clear();
                node.getChildren().addAll(last.getChildren());
            } else {
                node.getChildren().clear();
                node.getChildren().add(last);
            }
        }

    }

    private static void addAllFolderNode(Queue<FileTree> queue, Set<FileTree> children) {
        for (FileTree child : children) {
            if (!(child instanceof FolderNode)) {
                break;
            }

            queue.add(child);
        }
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

        protected final String topName;
        private List<String> extNames;
        private String text;

        public FolderNode(String topName) {
            super(FolderType.TYPE, null);
            this.topName = topName;
        }

        public String getTopName() {
            return topName;
        }

        public void appendName(String name) {
            if (extNames == null) {
                extNames = new ArrayList<>();
            }
            extNames.add(name);
            text = null;
        }

        @Override
        public String getText() {
            if (text == null) {
                if (extNames == null) {
                    text = topName;
                } else {
                    StringBuilder builder = new StringBuilder();
                    builder.append(topName);
                    for (String extName : extNames) {
                        builder.append('/').append(extName);
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
