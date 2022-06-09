package org.glavo.viewer.file.types.zip;

import org.glavo.viewer.file.Container;
import org.glavo.viewer.file.FileHandle;
import org.glavo.viewer.file.LocalFilePath;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.Enumeration;
import java.util.Set;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FallbackArchiveContainer extends Container {
    private final ZipFile file;
    private final TreeMap<LocalFilePath, ZipEntry> map = new TreeMap<>();


    public FallbackArchiveContainer(FileHandle handle, ZipFile file) {
        super(handle);
        this.file = file;

        LocalFilePath parentPath = handle.getPath();

        Enumeration<? extends ZipEntry> it = file.entries();
        while (it.hasMoreElements()) {
            ZipEntry entry = it.nextElement();

            if (!entry.isDirectory()) {
                map.put(LocalFilePath.of(entry.getName(), false, parentPath), entry);
            }
        }
    }

    public ZipFile getZipFile() {
        return file;
    }

    @Override
    protected synchronized FileHandle openFileImpl(LocalFilePath path) throws IOException {
        ZipEntry entry = map.get(path);
        if (entry == null) {
            throw new NoSuchFileException(path.toString());
        }

        return new FallbackArchiveFileHandle(this, path, entry);
    }

    @Override
    public Set<LocalFilePath> list(LocalFilePath dir) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void closeImpl() throws IOException {
        file.close();
    }
}