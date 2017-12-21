package org.glavo.viewer.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

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

}
