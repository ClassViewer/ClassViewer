package org.glavo.viewer.file.types.java.classfile.jvm.type;

import kala.value.primitive.IntRef;

import java.util.List;
import java.util.stream.Collectors;

public abstract class JavaTypes {
    private JavaTypes() {
    }

    public static final PrimitiveType VOID = new PrimitiveType("void", "V");

    public static final PrimitiveType BYTE = new PrimitiveType("byte", "B");
    public static final PrimitiveType CHAR = new PrimitiveType("char", "C");
    public static final PrimitiveType DOUBLE = new PrimitiveType("double", "D");
    public static final PrimitiveType FLOAT = new PrimitiveType("float", "F");
    public static final PrimitiveType INT = new PrimitiveType("int", "I");
    public static final PrimitiveType LONG = new PrimitiveType("long", "J");
    public static final PrimitiveType SHORT = new PrimitiveType("short", "S");
    public static final PrimitiveType BOOLEAN = new PrimitiveType("boolean", "Z");

    public static ArrayType newArrayType(JavaType baseType, int level) {
        assert level >= 1;

        String qualified = baseType.getQualified() + "[]".repeat(level);
        String descriptor = "[".repeat(level) + baseType.getDescriptor();

        return new ArrayType(baseType, level, qualified, descriptor);
    }

    public static MethodType newMethodType(JavaType returnType, List<JavaType> parameterTypes) {
        String qualified = parameterTypes.stream().map(JavaType::getQualified).collect(Collectors.joining(
                ", ", "(", "): " + returnType.getQualified()
        ));

        String descriptor = parameterTypes.stream().map(JavaType::getDescriptor).collect(Collectors.joining(
                "", "(", ")" + returnType.getDescriptor()
        ));

        return new MethodType(returnType, parameterTypes, qualified, descriptor);
    }

    public static JavaType parseDescriptor(String descriptor) {
        if (descriptor == null || descriptor.isEmpty()) throw new IllegalArgumentException("descriptor is empty");

        IntRef length = new IntRef();

        if (descriptor.charAt(0) == '(') {
            // TODO
        } else {
            JavaType type = scanDescriptor(descriptor, 0, length);
            if (length.value == descriptor.length()) return type;
        }

        throw new IllegalArgumentException("malformed descriptor '" + descriptor + "'");
    }

    public static JavaType scanDescriptor(String descriptor, int begin, IntRef length) {
        int len = descriptor.length() - begin;
        if (len == 0) throw new IllegalArgumentException("descriptor is empty");

        switch (descriptor.charAt(0)) {
            case 'B':
                return BYTE;
            case 'C':
                return CHAR;
            case 'D':
                return DOUBLE;
            case 'F':
                return FLOAT;
            case 'I':
                return INT;
            case 'J':
                return LONG;
            case 'S':
                return SHORT;
            case 'Z':
                return BOOLEAN;
            case 'L':
                int idx = descriptor.indexOf(';', begin + 1);
                if (idx >= 0) {

                }
                break;
            case '[':
                // TODO
                break;
        }

        throw new IllegalArgumentException("malformed descriptor '" + descriptor + "'");
    }
}
