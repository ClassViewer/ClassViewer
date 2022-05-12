package org.glavo.viewer.file.containers;

import kala.compress.archivers.zip.ZipArchiveEntry;
import kala.compress.archivers.zip.ZipArchiveReader;
import org.glavo.viewer.file.Container;
import org.glavo.viewer.file.FileHandle;
import org.glavo.viewer.file.FileStub;
import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.file.stubs.ArchiveFileStub;
import org.glavo.viewer.util.StringUtils;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.Iterator;
import java.util.NavigableSet;
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
                map.put(new FilePath(StringUtils.spiltPath(entry.getName()), parentPath), entry);
            }
        }
    }

    public ZipArchiveReader getReader() {
        return reader;
    }

    @Override
    protected synchronized FileStub openFileImpl(FilePath path) throws IOException {
        ZipArchiveEntry entry = map.get(path);
        if (entry == null) {
            throw new NoSuchFileException(path.toString());
        }

        return new ArchiveFileStub(this, path, entry);
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
