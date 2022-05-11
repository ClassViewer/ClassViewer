package org.glavo.viewer.file.containers;

import kala.compress.archivers.zip.ZipArchiveEntry;
import kala.compress.archivers.zip.ZipArchiveReader;
import org.glavo.viewer.file.Container;
import org.glavo.viewer.file.FileStubs;
import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.file.stubs.ArchiveFileStubs;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.NavigableSet;
import java.util.TreeMap;

public class ArchiveContainer extends Container {
    private final ZipArchiveReader reader;
    private final TreeMap<FilePath, ZipArchiveEntry> map = new TreeMap<>();


    public ArchiveContainer(FileStubs handle, ZipArchiveReader reader) {
        super(handle);
        this.reader = reader;


    }

    public ZipArchiveReader getReader() {
        return reader;
    }

    @Override
    protected synchronized FileStubs openFileImpl(FilePath path) throws IOException {
        ZipArchiveEntry entry = map.get(path);
        if (entry == null) {
            throw new NoSuchFileException(path.toString());
        }

        return new ArchiveFileStubs(this, path, entry);
    }

    @Override
    public NavigableSet<FilePath> resolveFiles() throws Exception {
        return map.navigableKeySet();
    }

    @Override
    public void closeImpl() throws IOException {
        reader.close();
    }
}
