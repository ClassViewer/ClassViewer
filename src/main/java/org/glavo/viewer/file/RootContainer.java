package org.glavo.viewer.file;

import org.glavo.viewer.file.Container;
import org.glavo.viewer.file.FileHandle;

public abstract class RootContainer extends Container {
    protected RootContainer(FileHandle handle) {
        super(handle);
    }
}