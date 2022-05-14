package org.glavo.viewer.file.containers;

import kala.compress.archivers.zip.ZipArchiveEntry;
import kala.compress.archivers.zip.ZipArchiveReader;
import org.glavo.viewer.file.Container;
import org.glavo.viewer.file.FileHandle;
import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.file.FileStub;
import org.glavo.viewer.file.stubs.ArchiveFileStub;
import org.glavo.viewer.file.stubs.FallbackArchiveFileStub;
import org.glavo.viewer.util.StringUtils;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.Enumeration;
import java.util.NavigableSet;
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
    protected synchronized FileStub openFileImpl(FilePath path) throws IOException {
        ZipEntry entry = map.get(path);
        if (entry == null) {
            throw new NoSuchFileException(path.toString());
        }

        return new FallbackArchiveFileStub(this, path, entry);
    }

    @Override
    public NavigableSet<FilePath> resolveFiles() throws Exception {
        return map.navigableKeySet();
    }

    @Override
    public void closeImpl() throws IOException {
        file.close();
    }
}