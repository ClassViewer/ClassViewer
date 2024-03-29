package org.glavo.viewer.file.types.tar;

import org.glavo.viewer.file.Container;
import org.glavo.viewer.file.FileHandle;
import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.file.types.ContainerFileType;
import org.glavo.viewer.file.types.zip.ArchiveFileType;

public final class TarFileType extends ContainerFileType {
    public static final TarFileType TYPE = new TarFileType();

    private TarFileType() {
        super("tar", ArchiveFileType.TYPE.getImage());
    }

    @Override
    public boolean check(FilePath path) {
        return switch (path.getFileNameExtension()) {
            case "tar" -> true;
            case "gz", "xz" ->
                    path.getFileName().startsWith(".tar", path.getFileName().length() - (path.getFileNameExtension().length() + 5));
            default -> false;
        };
    }

    @Override
    public Container openContainerImpl(FileHandle handle) throws Throwable {
        throw new UnsupportedOperationException(); // TODO
    }
}
