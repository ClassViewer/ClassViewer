package org.glavo.viewer.file.types.java.classfile.attribute;

import org.glavo.viewer.file.types.java.classfile.ClassFileComponent;
import org.glavo.viewer.file.types.java.classfile.ClassFileReader;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantInfo;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantMethodHandleInfo;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantUtf8Info;
import org.glavo.viewer.file.types.java.classfile.datatype.CpIndex;
import org.glavo.viewer.file.types.java.classfile.datatype.U4;

import java.io.IOException;

/*
BootstrapMethods_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u2 num_bootstrap_methods;
    {   u2 bootstrap_method_ref;
        u2 num_bootstrap_arguments;
        u2 bootstrap_arguments[num_bootstrap_arguments];
    } bootstrap_methods[num_bootstrap_methods];
}
 */
public final class BootstrapMethodsAttribute extends AttributeInfo {
    public static BootstrapMethodsAttribute readFrom(ClassFileReader reader, CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength) throws IOException {
        var attribute = new BootstrapMethodsAttribute(attributeNameIndex, attributeLength);
        attribute.readU2TableLength(reader, "num_bootstrap_methods");
        attribute.readTable(reader, "bootstrap_methods", BootstrapMethodInfo::readFrom);
        return attribute;
    }

    private BootstrapMethodsAttribute(CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength) {
        super(attributeNameIndex, attributeLength);
    }

    public static final class BootstrapMethodInfo extends ClassFileComponent {
        public static BootstrapMethodInfo readFrom(ClassFileReader reader) throws IOException {
            BootstrapMethodInfo bootstrapMethodInfo = new BootstrapMethodInfo();
            bootstrapMethodInfo.readCpIndex(reader, "bootstrap_method_ref", ConstantMethodHandleInfo.class);
            bootstrapMethodInfo.readU2TableLength(reader, "num_bootstrap_arguments");
            bootstrapMethodInfo.readTable(reader, "bootstrap_arguments", it -> it.readCpIndex(ConstantInfo.class), true);
            return bootstrapMethodInfo;
        }
    }
}
