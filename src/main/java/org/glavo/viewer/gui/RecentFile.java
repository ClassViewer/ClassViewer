package org.glavo.viewer.gui;

import org.glavo.viewer.gui.filetypes.FileType;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public final class RecentFile {
    public static RecentFile parse(String s) throws MalformedURLException {
        Objects.requireNonNull(s);
        if (!s.contains("#=>"))
            return null;
        return new RecentFile(FileType.valueOf(s.split("#=>")[0]),
                new URL(s.split("#=>")[1]));
    }

    public final FileType type;
    public final URL url;

    public RecentFile(FileType type, URL url) {
        this.type = type;
        this.url = url;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof RecentFile && (((RecentFile)obj).type == type) && (((RecentFile)obj).url.equals(url));
    }

    @Override
    public String toString() {
        return type + "#=>" + url;
    }
}
