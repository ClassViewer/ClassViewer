package org.glavo.viewer.util;

import kala.compress.archivers.zip.ZipArchiveEntry;
import kala.compress.archivers.zip.ZipArchiveReader;
import kala.compress.utils.Charsets;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.*;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class ZipUtils {
    private static final Set<Charset> commonCharacterSets;

    static {
        Set<Charset> charsets = new LinkedHashSet<>();
        charsets.add(StandardCharsets.UTF_8);

        if (!Charsets.nativeCharset().name().startsWith("GB")) {
            charsets.add(Charsets.nativeCharset());
        }

        try {
            charsets.add(Charset.forName("GB18030"));
        } catch (UnsupportedCharsetException e) {
            charsets.add(Charset.forName("GBK"));
        }

        try {
            charsets.add(Charset.forName("Shift_JIS"));
        } catch (UnsupportedCharsetException ignored) {
        }

        try {
            charsets.add(Charset.forName("Big5"));
        } catch (UnsupportedCharsetException ignored) {
        }

        charsets.add(StandardCharsets.ISO_8859_1);
        charsets.add(StandardCharsets.UTF_16LE);
        charsets.add(StandardCharsets.UTF_16BE);

        commonCharacterSets = charsets;
    }

    public static Charset testEncoding(ZipArchiveReader reader) {
        Iterator<ZipArchiveEntry> it = reader.getEntriesIterator();
        if (!it.hasNext() || it.next().getGeneralPurposeBit().usesUTF8ForNames()) {
            return StandardCharsets.UTF_8;
        }

        for (Charset charset : commonCharacterSets) {
            if (testEncoding(reader, charset)) {
                return charset;
            }
        }

        return null;
    }

    public static boolean testEncoding(ZipArchiveReader reader, Charset charset) {
        Iterator<ZipArchiveEntry> entries = reader.getEntriesIterator();

        CharsetDecoder cd = charset.newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
        CharBuffer cb = CharBuffer.allocate(32);

        while (entries.hasNext()) {
            ZipArchiveEntry entry = entries.next();

            if (entry.getGeneralPurposeBit().usesUTF8ForNames()) {
                continue;
            }

            cd.reset();
            byte[] ba = entry.getRawName();
            int clen = (int) (ba.length * cd.maxCharsPerByte());
            if (clen == 0) {
                continue;
            }
            if (clen <= cb.capacity()) {
                cb.clear();
            } else {
                cb = CharBuffer.allocate(clen);
            }

            ByteBuffer bb = ByteBuffer.wrap(ba, 0, ba.length);
            CoderResult cr = cd.decode(bb, cb, true);
            if (!cr.isUnderflow()) {
                return false;
            }
            cr = cd.flush(cb);
            if (!cr.isUnderflow()) {
                return false;
            }
        }
        return true;
    }

}
