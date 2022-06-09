package org.glavo.viewer.file.types.zip;

import org.glavo.viewer.file.Container;
import org.glavo.viewer.file.FileHandle;
import org.glavo.viewer.file.OldFilePath;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.Enumeration;
import java.util.Set;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FallbackArchiveContainer extends Container {
    private final ZipFile file;
    private final TreeMap<OldFilePath, ZipEntry> map = new TreeMap<>();


    public FallbackArchiveContainer(FileHandle handle, ZipFile file) {
        super(handle);
        this.file = file;

        OldFilePath parentPath = handle.getPath();

        Enumeration<? extends ZipEntry> it = file.entries();
        while (it.hasMoreElements()) {
            ZipEntry entry = it.nextElement();

            if (!entry.isDirectory()) {
                map.put(OldFilePath.of(entry.getName(), false, parentPath), entry);
            }
        }
    }

    public ZipFile getZipFile() {
        return file;
    }

    @Override
    protected synchronized FileHandle openFileImpl(OldFilePath path) throws IOException {
        ZipEntry entry = map.get(path);
        if (entry == null) {
            throw new NoSuchFileException(path.toString());
        }

        return new FallbackArchiveFileHandle(this, path, entry);
    }

    @Override
    public Set<OldFilePath> list(OldFilePath dir) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void closeImpl() throws IOException {
        file.close();
    }
}