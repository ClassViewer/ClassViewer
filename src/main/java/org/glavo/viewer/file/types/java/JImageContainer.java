package org.glavo.viewer.file.types.java;

import org.glavo.jimage.ImageReader;
import org.glavo.viewer.file.Container;
import org.glavo.viewer.file.FileHandle;
import org.glavo.viewer.file.FilePath;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.Set;
import java.util.TreeMap;

public class JImageContainer extends Container {
    private static final int offset = "/modules/".length();

    private final ImageReader reader;
    private final TreeMap<FilePath, ImageReader.Node> map = new TreeMap<>();


    public JImageContainer(FileHandle handle, ImageReader reader) throws IOException {
        super(handle);
        this.reader = reader;

        reader.getModuleNames();
        buildDirTree(reader.findNode("/modules"));
    }

    private void buildDirTree(ImageReader.Node node) throws IOException {
        assert node.getName().startsWith("/modules/");
        if (node.isResource()) {
            map.put(FilePath.of(node.getName().substring(offset), false, getPath()), node);
        }

        if (node.isDirectory()) {
            if (!node.isCompleted()) {
                reader.findNode(node.getName());
            }

            for (ImageReader.Node child : node.getChildren()) {
                buildDirTree(child);
            }
        }
    }

    public ImageReader getReader() {
        return reader;
    }

    @Override
    protected synchronized FileHandle openFileImpl(FilePath path) throws IOException {
        ImageReader.Node node = map.get(path);

        if (node == null) {
            throw new NoSuchFileException(path.toString());
        }

        return new JImageFileHandle(this, path, reader, node);
    }

    @Override
    public Set<FilePath> list(FilePath dir) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void closeImpl() throws IOException {
        reader.close();
    }
}
