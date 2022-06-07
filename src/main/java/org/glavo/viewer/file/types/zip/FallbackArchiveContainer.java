package org.glavo.viewer.file.types.zip;

import org.glavo.viewer.file.Container;
import org.glavo.viewer.file.FileHandle;
import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.file.types.zip.FallbackArchiveFileHandle;
import org.glavo.viewer.util.StringUtils;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.Enumeration;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FallbackArchiveContainer extends Container {
    private final ZipFile file;
    private final TreeMap<FilePath, ZipEntry> map = new TreeMap<>();


    public FallbackArchiveContainer(FileHandle handle, ZipFile file) {
        super(handle);
        this.file = file;

        FilePath parentPath = handle.getPath();

        Enumeration<? extends ZipEntry> it = file.entries();
        while (it.hasMoreElements()) {
            ZipEntry entry = it.nextElement();

            if (!entry.isDirectory()) {
                map.put(new FilePath(StringUtils.spiltPath(entry.getName()), parentPath), entry);
            }
        }
    }

    public ZipFile getZipFile() {
        return file;
    }

    @Override
    protected synchronized FileHandle openFileImpl(FilePath path) throws IOException {
        ZipEntry entry = map.get(path);
        if (entry == null) {
            throw new NoSuchFileException(path.toString());
        }

        return new FallbackArchiveFileHandle(this, path, entry);
    }

    @Override
    public NavigableSet<FilePath> resolveFiles() throws Exception {
        return map.navigableKeySet();
    }

    @Override
    public Set<FilePath> list(FilePath dir) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void closeImpl() throws IOException {
        file.close();
    }
}