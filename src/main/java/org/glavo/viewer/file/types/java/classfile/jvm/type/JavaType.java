package org.glavo.viewer.file.types.java.classfile.jvm.type;

public abstract sealed class JavaType permits PrimitiveType, ArrayType, ClassType, MethodType {
    private final String qualified;
    private final String descriptor;

    JavaType(String qualified, String descriptor) {
        this.qualified = qualified;
        this.descriptor = descriptor;
    }

    public boolean isMethodType() {
        return this instanceof MethodType;
    }

    public String getQualified() {
        return qualified;
    }

    public String getDescriptor() {
        return descriptor;
    }

    @Override
    public int hashCode() {
        return descriptor.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof JavaType other && this.getDescriptor().equals(((JavaType) obj).getDescriptor());
    }

    @Override
    public String toString() {
        return getQualified();
    }
}
