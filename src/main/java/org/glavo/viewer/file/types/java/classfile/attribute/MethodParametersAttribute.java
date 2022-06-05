package org.glavo.viewer.file.types.java.classfile.attribute;

import org.glavo.viewer.file.types.java.classfile.ClassFileComponent;
import org.glavo.viewer.file.types.java.classfile.ClassFileReader;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantUtf8Info;
import org.glavo.viewer.file.types.java.classfile.datatype.CpIndex;
import org.glavo.viewer.file.types.java.classfile.datatype.U4;
import org.glavo.viewer.file.types.java.classfile.jvm.AccessFlagType;

import java.io.IOException;

/*
MethodParameters_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u1 parameters_count;
    {   u2 name_index;
        u2 access_flags;
    } parameters[parameters_count];
}
 */
public final class MethodParametersAttribute extends AttributeInfo {
    public static MethodParametersAttribute readFrom(ClassFileReader reader, CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength) throws IOException {
        var attribute = new MethodParametersAttribute(attributeNameIndex, attributeLength);
        attribute.readU1TableLength(reader, "parameters_count");
        attribute.readTable(reader, "parameters", MethodParametersAttribute.ParameterInfo::readFrom, true);
        return attribute;
    }

    private MethodParametersAttribute(CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength) {
        super(attributeNameIndex, attributeLength);
    }

    public static final class ParameterInfo extends ClassFileComponent {
        public static ParameterInfo readFrom(ClassFileReader reader) throws IOException {
            ParameterInfo methodParametersInfo = new ParameterInfo();
            methodParametersInfo.readCpIndex(reader, "name_index", ConstantUtf8Info.class);
            methodParametersInfo.readAccessFlags(reader, "access_flags", AccessFlagType.AF_ALL);
            return methodParametersInfo;
        }
    }
}
