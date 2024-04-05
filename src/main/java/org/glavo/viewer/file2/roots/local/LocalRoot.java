package org.glavo.viewer.file2.roots.local;

import org.glavo.viewer.file2.VirtualRoot;

public final class LocalRoot extends VirtualRoot {
    public static final LocalRoot ROOT = new LocalRoot();

    @Override
    public String toString() {
        return "LocalRoot";
    }
}
