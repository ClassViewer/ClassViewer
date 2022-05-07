package org.glavo.viewer.file.containers;

import kala.compress.archivers.zip.ZipArchiveEntry;
import kala.compress.archivers.zip.ZipArchiveReader;
import org.glavo.viewer.file.Container;
import org.glavo.viewer.file.FileHandle;
import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.util.StringUtils;

import java.io.IOException;
import java.util.Iterator;
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

        Iterator<ZipArchiveEntry> it = reader.getEntriesIterator();
        while (it.hasNext()) {
            ZipArchiveEntry entry = it.next();
            if (!entry.isDirectory() && !entry.isUnixSymlink()) {
                res.add(new FilePath(StringUtils.spiltPath(entry.getName()), getPath()));
            }
        }
        return res;
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
