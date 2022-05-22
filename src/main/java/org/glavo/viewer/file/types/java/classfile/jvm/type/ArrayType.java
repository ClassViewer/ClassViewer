package org.glavo.viewer.file.types.java.classfile.jvm.type;

public final class ArrayType extends JavaType {
    private final JavaType baseType;
    private final int level;

    ArrayType(JavaType baseType, int level, String qualified, String descriptor) {
        super(qualified, descriptor);
        this.baseType = baseType;
        this.level = level;
    }

    public JavaType getBaseType() {
        return baseType;
    }

    public int getLevel() {
        return level;
    }
}
