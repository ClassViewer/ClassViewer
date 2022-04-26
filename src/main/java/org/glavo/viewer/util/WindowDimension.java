package org.glavo.viewer.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public final class WindowDimension {
    private final boolean maximized;
    private final double width;
    private final double height;

    public WindowDimension(double width, double height) {
        this(false, width, height);
    }

    @JsonCreator
    public WindowDimension(
            @JsonProperty("maximized") boolean maximized,
            @JsonProperty("width") double width,
            @JsonProperty("height") double height) {
        this.maximized = maximized;
        this.width = width;
        this.height = height;
    }

    public boolean isMaximized() {
        return maximized;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
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
        return "WindowDimension[" + "isMaximized=" + maximized + ", " + "getWidth=" + width + ", " + "getHeight=" + height + ']';
    }

}
