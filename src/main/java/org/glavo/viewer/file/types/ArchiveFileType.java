package org.glavo.viewer.file.types;

import org.glavo.viewer.file.Container;
import org.glavo.viewer.file.FileHandle;
import org.glavo.viewer.file.FilePath;

import java.io.IOException;

public final class ArchiveFileType extends ContainerFileType {
    public static final ArchiveFileType TYPE = new ArchiveFileType();

    private ArchiveFileType() {
        super("archive");
    }

    @Override
    public boolean check(FilePath path) {
        switch (path.getFileNameExtension()) {
            case "zip":
            case "jar":
            case "jmod":
                return true;
        }
        return path.getFileName().equals("ct.sym");
    }

    @Override
    public Container openContainerImpl(FileHandle handle) throws IOException {
        throw new UnsupportedOperationException(); // TODO
    }
}
