package org.glavo.viewer.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Path;

public class UrlUtils {

    public static byte[] readData(URL url) throws IOException {
        try (InputStream is = url.openStream()) {
            byte[] data = new byte[is.available()];
            int len = 0;
            while (len < data.length) {
                len += is.read(data, len, data.length - len);
            }
            return data;
        }
    }

    public static String getFileName(URL url) {
        String[] arr = url.toString().split("/");
        return arr[arr.length - 1];
    }

    @SuppressWarnings("deprecation")
    public static URL pathToUrl(Path path) throws MalformedURLException {
        if(path == null)
            return null;

        return new URL(URLDecoder.decode(path.toUri().toString()));
    }

}
