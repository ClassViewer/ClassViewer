package org.glavo.viewer.file.types;

import kala.compress.archivers.zip.ZipArchiveReader;
import org.glavo.viewer.file.Container;
import org.glavo.viewer.file.FileStubs;
import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.file.containers.ArchiveContainer;
import org.glavo.viewer.util.ZipUtils;

import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class ArchiveFileType extends ContainerFileType {
    public static final ArchiveFileType TYPE = new ArchiveFileType();

    private ArchiveFileType() {
        super("archive");
    }

    @Override
    public boolean check(FilePath path) {
        switch (path.getFileNameExtension()) {
            case "zip":
            case "jar":
            case "jmod":
                return true;
        }
        return path.getFileName().equals("ct.sym");
    }

    @Override
    public Container openContainerImpl(FileStubs handle) throws Throwable {
        SeekableByteChannel channel = null;
        try {
            channel = handle.openChannel();
            ZipArchiveReader reader = new ZipArchiveReader(channel);
            Charset charset = ZipUtils.testEncoding(reader);
            if (charset != StandardCharsets.UTF_8 && charset != null) {
                reader = new ZipArchiveReader(channel, charset);
            }
            return new ArchiveContainer(handle, reader);
        } catch (Throwable e) {
            if (channel != null) {
                channel.close();
            }
            throw e;
        }
    }
}
