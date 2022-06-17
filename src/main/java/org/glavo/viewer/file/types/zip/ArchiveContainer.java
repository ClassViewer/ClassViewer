package org.glavo.viewer.file.types.zip;

import kala.compress.archivers.zip.ZipArchiveEntry;
import kala.compress.archivers.zip.ZipArchiveReader;
import org.glavo.viewer.file.Container;
import org.glavo.viewer.file.FileHandle;
import org.glavo.viewer.file.FilePath;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

public class ArchiveContainer extends Container {
    private final ZipArchiveReader reader;
    private final TreeMap<FilePath, ZipArchiveEntry> map = new TreeMap<>();


    public ArchiveContainer(FileHandle handle, ZipArchiveReader reader) {
        super(handle);
        this.reader = reader;

        FilePath parentPath = handle.getPath();

        Iterator<ZipArchiveEntry> it = reader.getEntriesIterator();
        while (it.hasNext()) {
            ZipArchiveEntry entry = it.next();

            if (!entry.isDirectory() && !entry.isUnixSymlink()) {
                map.put(FilePath.of(entry.getName(), false, parentPath), entry);
            }
        }
    }

    public ZipArchiveReader getReader() {
        return reader;
    }

    @Override
    protected synchronized FileHandle openFileImpl(FilePath path) throws IOException {
        ZipArchiveEntry entry = map.get(path);
        if (entry == null) {
            throw new NoSuchFileException(path.toString());
        }
        return new ArchiveFileHandle(this, path, entry);
    }

    @Override
    public Set<FilePath> list(FilePath dir) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void closeImpl() throws IOException {
        reader.close();
    }
}
