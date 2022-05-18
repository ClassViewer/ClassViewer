package org.glavo.viewer.file.types.java;

import org.glavo.jimage.ImageReader;
import org.glavo.viewer.file.Container;
import org.glavo.viewer.file.FileHandle;
import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.file.types.ContainerFileType;

import java.io.IOException;
import java.nio.file.Paths;

public class JImageFileType extends ContainerFileType {
    public static final JImageFileType TYPE = new JImageFileType();

    private JImageFileType() {
        super("jimage");
    }

    @Override
    public boolean check(FilePath path) {
        return path.getParent().isLocalFile() && path.getFileName().equals("modules");
    }

    @Override
    public Container openContainerImpl(FileHandle handle) throws IOException {
        assert handle.getPath().isLocalFile();

        return new JImageContainer(handle, ImageReader.open(Paths.get(handle.getPath().getPath())));
    }
}
