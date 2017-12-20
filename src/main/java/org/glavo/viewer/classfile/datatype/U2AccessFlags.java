package org.glavo.viewer.classfile.datatype;


import org.glavo.viewer.classfile.jvm.AccessFlags;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class U2AccessFlags extends UInt {

    public U2AccessFlags(int afType) {
        super(READ_U2, (val, cp) -> describe(afType, val));
    }

    private static String describe(int flagsType, int flagsVal) {
        return Stream.of(AccessFlags.values())
                .filter(flag -> (flag.type & flagsType) != 0)
                .filter(flag -> (flag.flag & flagsVal) != 0)
                .map(Object::toString)
                .collect(Collectors.joining(", "));
    }

    public boolean isInterface() {
        return (getIntValue() & AccessFlags.ACC_INTERFACE.flag)!= 0;
    }

    public boolean isEnum() {
        return (getIntValue() & AccessFlags.ACC_ENUM.flag)!= 0;
    }

    public boolean isAbstract() {
        return (getIntValue() & AccessFlags.ACC_ABSTRACT.flag)!= 0;
    }

    public boolean isAnnotation() {
        return (getIntValue() & AccessFlags.ACC_ANNOTATION.flag)!= 0;
    }

    public boolean isFinal() {
        return (getIntValue() & AccessFlags.ACC_FINAL.flag)!= 0;
    }
}
