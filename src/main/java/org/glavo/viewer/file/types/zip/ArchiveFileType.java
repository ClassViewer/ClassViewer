package org.glavo.viewer.file.types.zip;

import kala.compress.archivers.zip.ZipArchiveReader;
import kala.compress.utils.IOUtils;
import org.glavo.viewer.file.Container;
import org.glavo.viewer.file.FileHandle;
import org.glavo.viewer.file.OldFilePath;
import org.glavo.viewer.file.types.ContainerFileType;
import org.glavo.viewer.util.ZipUtils;

import java.io.File;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.zip.ZipFile;

import static org.glavo.viewer.util.Logging.LOGGER;

public final class ArchiveFileType extends ContainerFileType {
    public static final ArchiveFileType TYPE = new ArchiveFileType();

    private ArchiveFileType() {
        super("archive");
    }

    @Override
    public boolean check(OldFilePath path) {
        switch (path.getFileNameExtension()) {
            case "zip":
            case "jar":
            case "jmod":
                return true;
        }
        return path.getFileName().equals("ct.sym");
    }

    @Override
    public Container openContainerImpl(FileHandle handle) throws Throwable {
        SeekableByteChannel channel = null;
        try {
            channel = handle.openChannel();
            ZipArchiveReader reader = new ZipArchiveReader(channel);
            Charset charset = ZipUtils.testEncoding(reader);
            if (charset != StandardCharsets.UTF_8 && charset != null) {
                reader = new ZipArchiveReader(channel, charset);
            }

            // try fallback
            if (handle.getPath().isLocalFile()
                    && !reader.getEntriesIterator().hasNext()
                    && channel.size() > 22) {
                ZipFile zf = null;
                try {
                    File f = new File(handle.getPath().getPath());
                    if (f.isFile()) {
                        zf = new ZipFile(f);

                        if (zf.entries().hasMoreElements()) {
                            IOUtils.closeQuietly(reader);
                            return new FallbackArchiveContainer(handle, zf);
                        }
                    }
                } catch (Throwable e) {
                    LOGGER.log(Level.WARNING, "Failed to open FallbackArchiveContainer", e);
                }
                IOUtils.closeQuietly(zf);
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
