package org.glavo.viewer.file.types;

import org.glavo.viewer.file.ContainerFileType;
import org.glavo.viewer.file.FileType;

public class ArchiveFileType extends FileType implements ContainerFileType {
    public ArchiveFileType() {
        super("archive");
    }
}
