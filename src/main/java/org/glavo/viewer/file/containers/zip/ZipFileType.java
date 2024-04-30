/*
 * Copyright 2024 Glavo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.glavo.viewer.file.containers.zip;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.glavo.chardet.DetectedCharset;
import org.glavo.chardet.UniversalDetector;
import org.glavo.viewer.file.ContainerFileType;
import org.glavo.viewer.file.FileHandle;
import org.glavo.viewer.resources.Images;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.Set;

public final class ZipFileType extends ContainerFileType {
    public static final ZipFileType TYPE = new ZipFileType();

    private ZipFileType() {
        super("zip", Images.archive, Set.of("zip", "jar", "war"));
    }

    @Override
    public ZipContainer openContainerImpl(FileHandle handle) throws IOException {
        SeekableByteChannel channel;

        if (handle.supportRandomAccess()) {
            channel = handle.getChannel();
        } else {
            Path tempFile = Files.createTempFile("viewer-", ".zip");
            tempFile.toFile().deleteOnExit();

            try (InputStream input = handle.getInputStream()) {
                Files.copy(input, tempFile, StandardCopyOption.REPLACE_EXISTING);
            }

            channel = Files.newByteChannel(tempFile);
        }

        try {


            ZipFile zipFile = ZipFile.builder()
                    .setSeekableByteChannel(channel)
                    .setCharset(StandardCharsets.UTF_8)
                    .get();

            UniversalDetector detector = null;
            Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
            while (entries.hasMoreElements()) {
                ZipArchiveEntry entry = entries.nextElement();
                if (entry.getGeneralPurposeBit().usesUTF8ForNames()) {
                    continue;
                }

                if (detector == null) {
                    detector = new UniversalDetector();
                }
                detector.handleData(entry.getRawName());
                if (detector.isDone()) {
                    break;
                }
            }

            if (detector != null) {
                detector.dataEnd();
                DetectedCharset detectedCharsetName = detector.getDetectedCharset();

                if (detectedCharsetName != null) {
                    Charset detectedCharset = Charset.forName(detectedCharsetName.getName(), StandardCharsets.UTF_8);
                    if (detectedCharset != StandardCharsets.UTF_8 && detectedCharset != StandardCharsets.US_ASCII) {
                        zipFile = ZipFile.builder()
                                .setSeekableByteChannel(channel)
                                .setCharset(detectedCharset)
                                .get();
                    }
                }
            }

            handle.setOnForceClose(zipFile::close);
            return new ZipContainer(handle, zipFile);
        } catch (Throwable e) {
            try {
                channel.close();
            } catch (Throwable ignored) {
            }
            throw e;
        }
    }
}
