package org.glavo.viewer.file.containers;

import kala.compress.archivers.zip.ZipArchiveReader;
import org.glavo.viewer.file.Container;
import org.glavo.viewer.file.FileHandle;
import org.glavo.viewer.file.FilePath;

import java.io.IOException;
import java.util.NavigableSet;
import java.util.TreeSet;

public class ArchiveContainer extends Container {
    private final ZipArchiveReader reader;

    protected ArchiveContainer(FileHandle handle, ZipArchiveReader reader) {
        super(handle);
        this.reader = reader;
    }

    @Override
    public NavigableSet<FilePath> resolveFiles() throws Exception {
        TreeSet<FilePath> res = new TreeSet<>();
        // TODO
        return res;
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
