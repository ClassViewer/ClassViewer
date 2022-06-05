package org.glavo.viewer.file.types.java.classfile.attribute;

import org.glavo.viewer.file.types.java.classfile.ClassFileReader;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantPackageInfo;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantUtf8Info;
import org.glavo.viewer.file.types.java.classfile.datatype.CpIndex;
import org.glavo.viewer.file.types.java.classfile.datatype.U4;

import java.io.IOException;

/*
ModulePackages_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u2 package_count;
    u2 package_index[package_count];
}
 */
public final class ModulePackagesAttribute extends AttributeInfo {
    public static ModulePackagesAttribute readFrom(ClassFileReader reader, CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength) throws IOException {
        var attribute = new ModulePackagesAttribute(attributeNameIndex, attributeLength);
        attribute.readU2TableLength(reader, "package_count");
        attribute.readTable(reader, "package_index", it -> it.readCpIndexEager(ConstantPackageInfo.class));
        return attribute;
    }

    private ModulePackagesAttribute(CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength) {
        super(attributeNameIndex, attributeLength);
    }
}
