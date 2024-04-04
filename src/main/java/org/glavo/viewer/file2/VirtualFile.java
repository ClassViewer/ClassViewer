package org.glavo.viewer.file2;

import org.glavo.viewer.file.Container;

import java.util.List;

public abstract class VirtualFile {

    public abstract Container getContainer();

    /**
     * @throws IllegalArgumentException if other is not a Path that can be relativized against this path
     */
    public abstract List<String> relativize(VirtualFile other);

    public abstract String getFileName();

    private String extension;

    public String getFileNameExtension() {
        if (extension == null) {
            String fn = getFileName();
            int idx = fn.lastIndexOf('.');
            extension = idx <= 0 ? "" : fn.substring(idx + 1);
        }

        return extension;
    }

    // ---

    public abstract boolean isDirectory();

}
