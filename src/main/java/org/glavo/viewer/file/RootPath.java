package org.glavo.viewer.file;

public abstract non-sealed class RootPath extends AbstractPath {
    public RootPath() {
    }

    protected int order() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode() ^ this.toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null
                && this.getClass() == obj.getClass()
                && this.toString().equals(obj.toString());
    }
}
