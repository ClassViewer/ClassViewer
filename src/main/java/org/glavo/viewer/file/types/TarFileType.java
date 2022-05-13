package org.glavo.viewer.file.types;

import org.glavo.viewer.file.Container;
import org.glavo.viewer.file.FileHandle;
import org.glavo.viewer.file.FilePath;

public final class TarFileType extends ContainerFileType {
    public static final TarFileType TYPE = new TarFileType();

    private TarFileType() {
        super("tar", ArchiveFileType.TYPE.getImage());
    }

    @Override
    public boolean check(FilePath path) {
        switch (path.getFileNameExtension()) {
            case "tar":
                return true;
            case "gz":
            case "xz":
                return path.getFileName().startsWith(".tar", path.getFileName().length() - (path.getFileNameExtension().length() + 5));
        }
        return false;
    }

    @Override
    public Container openContainerImpl(FileHandle handle) throws Throwable {
        throw new UnsupportedOperationException(); // TODO
    }
}
