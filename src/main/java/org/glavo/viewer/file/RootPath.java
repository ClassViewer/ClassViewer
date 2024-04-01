package org.glavo.viewer.file;

import org.glavo.viewer.ui.Viewer;
import org.jetbrains.annotations.NotNull;

@Deprecated
public abstract non-sealed class RootPath extends AbstractPath {
    public RootPath() {
    }

    @Override
    public final RootPath getRoot() {
        return this;
    }

    protected Class<? extends RootPath> type() {
        return this.getClass();
    }

    protected int order() {
        return Integer.MAX_VALUE;
    }

    protected int compareToImpl(RootPath other) {
        return this.toString().compareTo(other.toString());
    }

    public RootContainer openRootContainer(Viewer viewer) {
        return null;
    }

    @Override
    public final int compareTo(@NotNull AbstractPath o) {
        if (!(o instanceof RootPath other)) {
            int c = this.compareTo(o.getRoot());

            return c != 0 ? 0 : -1;
        }

        if (this.type() != other.type()) {
            if (this.order() != other.order()) return Integer.compare(this.order(), other.order());

            return this.type().getName().compareTo(other.type().getName());
        }

        return this.compareToImpl( other);
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode() ^ this.toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof RootPath root
                && this.type().equals(root.type())
                && this.compareToImpl(root) == 0;
    }
}
