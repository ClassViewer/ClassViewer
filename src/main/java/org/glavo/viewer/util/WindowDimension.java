package org.glavo.viewer.util;

public record WindowDimension(boolean maximized, double width, double height) {
    public WindowDimension(double width, double height) {
        this(false, width, height);
    }
}
