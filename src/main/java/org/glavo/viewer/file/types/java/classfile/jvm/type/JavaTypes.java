package org.glavo.viewer.file.types.java.classfile.jvm.type;

public abstract class JavaTypes {
    private JavaTypes() {
    }

    public static final PrimitiveType BYTE = new PrimitiveType("byte", "B");
    public static final PrimitiveType CHAR = new PrimitiveType("char", "C");
    public static final PrimitiveType DOUBLE = new PrimitiveType("double", "D");
    public static final PrimitiveType FLOAT = new PrimitiveType("float", "F");
    public static final PrimitiveType INT = new PrimitiveType("int", "I");
    public static final PrimitiveType LONG = new PrimitiveType("long", "J");
    public static final PrimitiveType SHORT = new PrimitiveType("short", "S");
    public static final PrimitiveType BOOLEAN = new PrimitiveType("boolean", "Z");

}
