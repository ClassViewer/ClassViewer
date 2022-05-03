package org.glavo.viewer.file.containers;

import org.glavo.viewer.file.Container;
import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.file.VirtualFile;

import java.io.IOException;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class FolderContainer extends Container {
    private NavigableMap<FilePath, VirtualFile> files;

    public FolderContainer(Container parent, FilePath path) {
        super(parent, path);
    }

    @Override
    public NavigableMap<FilePath, VirtualFile> resolveFiles() {
        if (files != null) {
            return files;
        }

        synchronized (this) {
            if (files != null) {
                return files;
            }

            Map<FilePath, VirtualFile> map = new TreeMap<>();


            // TODO

            return files;
        }
    }

    @Override
    public void close() throws IOException {

    }
}
