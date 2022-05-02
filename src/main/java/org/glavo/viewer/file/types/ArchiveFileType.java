package org.glavo.viewer.file.types;

import org.glavo.viewer.file.Container;
import org.glavo.viewer.file.FilePath;

import java.io.IOException;

public class ArchiveFileType extends ContainerFileType {
    public ArchiveFileType() {
        super("archive");
    }

    @Override
    public boolean check(FilePath path) {
        String fileName = path.getFileName();
        return fileName.endsWith(".zip")
                || fileName.endsWith(".jar")
                || fileName.endsWith(".jmod")
                || fileName.equals("ct.sym");
    }

    @Override
    public Container open(FilePath path) throws IOException {
        throw new UnsupportedOperationException(); // TODO
    }
}
