package org.glavo.viewer.file2;

import org.glavo.viewer.file.Container;

import java.util.List;

public interface FilePath {

    Container getContainer();

    /**
     * @throws IllegalArgumentException if other is not a Path that can be relativized against this path
     */
    List<String> relativize(FilePath other);
}
