package org.glavo.viewer.file.types;

import org.glavo.jimage.ImageReader;
import org.glavo.viewer.file.Container;
import org.glavo.viewer.file.FileHandle;
import org.glavo.viewer.file.FileStub;
import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.file.containers.JImageContainer;
import org.glavo.viewer.resources.Images;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JImageFileType extends ContainerFileType {
    public static final JImageFileType TYPE = new JImageFileType();

    private JImageFileType() {
        super("jimage");
    }

    @Override
    public boolean check(FilePath path) {
        return path.getParent() == null && path.getFileName().equals("modules");
    }

    @Override
    public Container openContainerImpl(FileHandle handle) throws IOException {
        assert handle.getPath().getParent() == null;

        return new JImageContainer(handle, ImageReader.open(Paths.get(handle.getPath().getPath())));
    }
}
