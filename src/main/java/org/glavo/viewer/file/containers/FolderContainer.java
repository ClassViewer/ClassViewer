package org.glavo.viewer.file.containers;

import org.glavo.viewer.file.Container;
import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.file.FileHandle;

import java.io.IOException;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeMap;

public class FolderContainer extends Container {
    private NavigableSet<FilePath> files;

    public FolderContainer(Container parent, FilePath path) {
        super(parent, path);
    }

    @Override
    public NavigableSet<FilePath> resolveFiles() {
        if (files != null) {
            return files;
        }

        synchronized (this) {
            if (files != null) {
                return files;
            }

            Map<FilePath, FileHandle> map = new TreeMap<>();


            // TODO

            return files;
        }
    }

    @Override
    public void close() throws IOException {

    }
}
