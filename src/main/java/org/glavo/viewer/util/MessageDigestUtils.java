package org.glavo.viewer.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MessageDigestUtils {
    public static String toString(byte[] arr) {
        StringBuilder builder = new StringBuilder(arr.length * 2);

        for (byte b : arr) {
            builder.append(HexText.byteToLowerString(b));
        }

        return builder.toString();
    }

    public static boolean checkFileSHA1(Path file, String hash) throws IOException {
        if (Files.notExists(file)) {
            return false;
        }

        Cache cache = Cache.getCache();

        try (InputStream input = Files.newInputStream(file)) {
            MessageDigest sha1 = cache.getSHA1();
            byte[] buffer = cache.getBuffer();

            int n;

            while ((n = input.read(buffer)) > 0) {
                sha1.update(buffer, 0, n);
            }

            return hash.equalsIgnoreCase(toString(sha1.digest()));
        }
    }

    private static final class Cache {
        private static final ThreadLocal<Cache> theadLocalCache = ThreadLocal.withInitial(Cache::new);

        static Cache getCache() {
            return theadLocalCache.get();
        }

        private MessageDigest sha1;
        private MessageDigest sha256;

        private byte[] buffer;

        MessageDigest getSHA1() {
            if (sha1 == null) {
                try {
                    sha1 = MessageDigest.getInstance("SHA-1");
                } catch (NoSuchAlgorithmException e) {
                    throw new AssertionError(e);
                }
            } else {
                sha1.reset();
            }
            return sha1;
        }

        MessageDigest getSHA256() {
            if (sha256 == null) {
                try {
                    sha256 = MessageDigest.getInstance("SHA-256");
                } catch (NoSuchAlgorithmException e) {
                    throw new AssertionError(e);
                }
            } else {
                sha256.reset();
            }
            return sha256;
        }

        byte[] getBuffer() {
            if (buffer == null) {
                buffer = new byte[8192];
            }
            return buffer;
        }
    }
}
