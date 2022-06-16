package org.glavo.viewer.file;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unchecked")
public abstract non-sealed class RootPath<R extends RootPath<R>> extends AbstractPath {
    public RootPath() {
    }

    @Override
    public R getRoot() {
        return (R) this;
    }

    protected Class<R> type() {
        return (Class<R>) this.getClass();
    }

    protected int order() {
        return Integer.MAX_VALUE;
    }

    protected int compareToImpl(R other) {
        return this.toString().compareTo(other.toString());
    }

    @Override
    public final int compareTo(@NotNull AbstractPath o) {
        if (!(o instanceof RootPath<?> other)) {
            int c = this.compareTo(o.getRoot());

            return c != 0 ? 0 : -1;
        }

        if (this.type() != other.type()) {
            if (this.order() != other.order()) return Integer.compare(this.order(), other.order());

            return this.type().getName().compareTo(other.type().getName());
        }

        return this.compareToImpl((R) other);
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
