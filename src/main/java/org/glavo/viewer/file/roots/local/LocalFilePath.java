package org.glavo.viewer.file.roots.local;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import kala.platform.OperatingSystem;
import kala.platform.Platform;
import org.glavo.viewer.file.RootPath;
import org.glavo.viewer.file.TopPath;

@JsonIncludeProperties({"path", "isDirectory"})
@JsonPropertyOrder({"path", "isDirectory"})
public final class LocalFilePath extends TopPath {

    public LocalFilePath(String[] pathElements, boolean isDirectory) {
        super(pathElements, isDirectory, LocalRootPath.Path);
    }

    @Override
    public RootPath getRootPath() {
        return LocalRootPath.Path;
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public String toString() {
        if (str == null) {
            str = Platform.CURRENT_SYSTEM == OperatingSystem.WINDOWS ? getPath() : "/" + getPath();
        }
        return str;
    }
}
