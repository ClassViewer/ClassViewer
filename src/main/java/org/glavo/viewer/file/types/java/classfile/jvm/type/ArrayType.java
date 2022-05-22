package org.glavo.viewer.file.types.java.classfile.jvm.type;

public final class ArrayType extends JavaType {
    private final JavaType baseType;

    ArrayType(JavaType baseType, String qualified, String descriptor) {
        super(qualified, descriptor);
        this.baseType = baseType;
    }

    public JavaType getBaseType() {
        return baseType;
    }
}
