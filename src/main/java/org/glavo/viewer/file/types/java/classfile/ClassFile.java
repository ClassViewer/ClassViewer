package org.glavo.viewer.file.types.java.classfile;

import kala.value.primitive.IntRef;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantPool;
import org.glavo.viewer.file.types.java.classfile.datatype.U2;
import org.glavo.viewer.file.types.java.classfile.datatype.U4Hex;
import org.glavo.viewer.file.types.java.classfile.jvm.AccessFlagType;

import java.io.IOException;

/*
ClassFile {
    u4             magic;
    u2             minor_version;
    u2             major_version;
    u2             constant_pool_count;
    cp_info        constant_pool[constant_pool_count-1];
    u2             access_flags;
    u2             this_class;
    u2             super_class;
    u2             interfaces_count;
    u2             interfaces[interfaces_count];
    u2             fields_count;
    field_info     fields[fields_count];
    u2             methods_count;
    method_info    methods[methods_count];
    u2             attributes_count;
    attribute_info attributes[attributes_count];
}
*/
public class ClassFile extends ClassFileComponent {

    public static ClassFile readFrom(ClassFileReader reader) throws IOException {
        ClassFile res = new ClassFile();
        res.setName("<In development>");

        U4Hex magic = res.readU4Hex(reader, "magic");
        if (magic.getIntValue() != 0xCAFEBABE) throw new ClassFileParseException("magic number mismatch: " + magic);

        res.readU2(reader, "minor_version");
        res.readU2(reader, "major_version");

        U2 cpCount = res.readU2(reader, "constant_pool_count");
        ConstantPool constantPool = ConstantPool.readFrom(reader, cpCount);
        res.getChildren().add(constantPool);
        res.setLength(reader.getOffset() - res.getOffset());

        res.readAccessFlags(reader, "access_flags", AccessFlagType.AF_CLASS);


        res.calculateOffset(new IntRef());
        return res;
    }


    public ConstantPool getConstantPool() {
        return (ConstantPool) getChildren().get(4);
    }
}
