package org.glavo.viewer.util;

import java.util.Objects;

public final class WindowDimension {
    private final boolean maximized;
    private final double width;
    private final double height;

    public WindowDimension(double width, double height) {
        this(false, width, height);
    }

    public WindowDimension(boolean maximized, double width, double height) {
        this.maximized = maximized;
        this.width = width;
        this.height = height;
    }

    public boolean maximized() {
        return maximized;
    }

    public double width() {
        return width;
    }

    public double height() {
        return height;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof WindowDimension)) {
            return false;
        }
        WindowDimension that = (WindowDimension) obj;
        return this.maximized == that.maximized && this.width == that.width && this.height == that.height;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maximized, width, height);
    }

    @Override
    public String toString() {
        return "WindowDimension[" + "maximized=" + maximized + ", " + "width=" + width + ", " + "height=" + height + ']';
    }

}
