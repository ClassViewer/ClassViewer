package org.glavo.viewer.file.types.java.classfile.jvm.type;

public abstract sealed class JavaType permits PrimitiveType, ArrayType, ClassType, MethodType {
    private final String qualified;
    private final String descriptor;

    JavaType(String qualified, String descriptor) {
        this.qualified = qualified;
        this.descriptor = descriptor;
    }

    public String getQualified() {
        return qualified;
    }

    public String getDescriptor() {
        return descriptor;
    }

    @Override
    public String toString() {
        return getQualified();
    }
}
