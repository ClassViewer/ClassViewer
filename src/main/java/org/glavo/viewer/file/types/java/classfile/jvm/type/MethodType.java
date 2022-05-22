package org.glavo.viewer.file.types.java.classfile.jvm.type;

import java.util.List;

public final class MethodType extends JavaType {
    private final JavaType returnType;
    private final List<JavaType> parameterTypes;

    MethodType(JavaType returnType, List<JavaType> parameterTypes, String qualified, String descriptor) {
        super(qualified, descriptor);
        this.returnType = returnType;
        this.parameterTypes = parameterTypes;
    }

    public JavaType getReturnType() {
        return returnType;
    }

    public List<JavaType> getParameterTypes() {
        return parameterTypes;
    }
}
