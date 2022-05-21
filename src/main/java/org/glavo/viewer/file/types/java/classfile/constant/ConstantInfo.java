package org.glavo.viewer.file.types.java.classfile.constant;

import org.glavo.viewer.file.types.java.classfile.ClassFileComponent;
import org.glavo.viewer.file.types.java.classfile.ClassFileParseException;
import org.glavo.viewer.file.types.java.classfile.ClassFileReader;
import org.glavo.viewer.file.types.java.classfile.datatype.Bytes;
import org.glavo.viewer.file.types.java.classfile.datatype.U1;
import org.glavo.viewer.file.types.java.classfile.datatype.U2;

import java.io.IOException;

public sealed abstract class ConstantInfo extends ClassFileComponent
        permits ConstantClassInfo,
        ConstantFieldrefInfo, ConstantMethodrefInfo, ConstantInterfaceMethodrefInfo,
        ConstantStringInfo, ConstantIntegerInfo, ConstantFloatInfo,
        ConstantLongInfo, ConstantDoubleInfo,
        ConstantNameAndTypeInfo,
        ConstantUtf8Info,
        ConstantMethodHandleInfo, ConstantMethodTypeInfo,
        ConstantInvokeDynamicInfo,
        ConstantModuleInfo, ConstantPackageInfo {
    //@formatter:off
    public static final int CONSTANT_Class              = 7;
    public static final int CONSTANT_Fieldref           = 9;
    public static final int CONSTANT_Methodref          = 10;
    public static final int CONSTANT_InterfaceMethodref = 11;
    public static final int CONSTANT_String             = 8;
    public static final int CONSTANT_Integer            = 3;
    public static final int CONSTANT_Float              = 4;
    public static final int CONSTANT_Long               = 5;
    public static final int CONSTANT_Double             = 6;
    public static final int CONSTANT_NameAndType        = 12;
    public static final int CONSTANT_Utf8               = 1;
    public static final int CONSTANT_MethodHandle       = 15;
    public static final int CONSTANT_MethodTypeInfo     = 16;
    public static final int CONSTANT_InvokeDynamic      = 18;
    public static final int CONSTANT_ModuleInfo         = 19;
    public static final int CONSTANT_PackageInfo        = 20;
    //@formatter:on

    public static ConstantInfo readFrom(ClassFileReader reader) throws IOException {
        int offset = reader.getOffset();

        U1 tag = reader.readU1();

        ConstantInfo info = switch (tag.getIntValue()) {
            case CONSTANT_Class -> new ConstantClassInfo(tag, reader.readU2());
            case CONSTANT_Fieldref -> new ConstantFieldrefInfo(tag, reader.readU2(), reader.readU2());
            case CONSTANT_Methodref -> new ConstantMethodrefInfo(tag, reader.readU2(), reader.readU2());
            case CONSTANT_InterfaceMethodref ->
                    new ConstantInterfaceMethodrefInfo(tag, reader.readU2(), reader.readU2());
            case CONSTANT_String -> new ConstantStringInfo(tag, reader.readU2());
            case CONSTANT_Integer -> new ConstantIntegerInfo(tag, reader.readU4());
            case CONSTANT_Float -> new ConstantFloatInfo(tag, reader.readU4());
            case CONSTANT_Long -> new ConstantLongInfo(tag, reader.readU4(), reader.readU4());
            case CONSTANT_Double -> new ConstantDoubleInfo(tag, reader.readU4(), reader.readU4());
            case CONSTANT_NameAndType -> new ConstantNameAndTypeInfo(tag, reader.readU2(), reader.readU2());
            case CONSTANT_Utf8 -> {
                U2 length = reader.readU2();
                byte[] bytes = reader.readNBytes(length.getIntValue());
                yield new ConstantUtf8Info(tag, length, new Bytes(bytes));
            }
            case CONSTANT_MethodHandle -> new ConstantMethodHandleInfo(tag, reader.readU1(), reader.readU2());
            case CONSTANT_MethodTypeInfo -> new ConstantMethodTypeInfo(tag, reader.readU2());
            case CONSTANT_InvokeDynamic -> new ConstantInvokeDynamicInfo(tag, reader.readU2(), reader.readU2());
            case CONSTANT_ModuleInfo -> new ConstantModuleInfo(tag, reader.readU2());
            case CONSTANT_PackageInfo -> new ConstantPackageInfo(tag, reader.readU2());
            default -> throw new ClassFileParseException("Unknown constant tag: " + tag.contentToString());
        };


        info.setLength(reader.getOffset() - offset);
        return info;
    }

    public ConstantInfo(U1 tag) {
        tag.setName("tag");
    }

    public U1 getTag() {
        return (U1) getChildren().get(0);
    }
}
