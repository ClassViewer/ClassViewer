package org.glavo.viewer.file.types;

import org.glavo.viewer.file.Container;
import org.glavo.viewer.file.FileHandle;
import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.resources.Images;

import java.io.IOException;

public class JImageFileType extends ContainerFileType {
    public static final JImageFileType TYPE = new JImageFileType();

    private JImageFileType() {
        super("jimage", Images.loadImage("fileTypes/file-archive.png"));
    }

    @Override
    public boolean check(FilePath path) {
        return path.getFileName().equals("modules");
    }

    @Override
    public Container openContainerImpl(FileHandle handle) throws IOException {
        throw new UnsupportedOperationException(); // TODO
    }
}
