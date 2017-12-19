package org.glavo.editor;

import java.nio.file.Path;

public interface FileType<T extends FileData> {
    FileType[] fileTypes = {

    };

    T parse(Path path);
}
