package org.glavo.viewer.file.types.java.classfile.attribute;

import org.glavo.viewer.file.types.java.classfile.ClassFileComponent;
import org.glavo.viewer.file.types.java.classfile.ClassFileReader;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantUtf8Info;
import org.glavo.viewer.file.types.java.classfile.datatype.CpIndex;
import org.glavo.viewer.file.types.java.classfile.datatype.Table;
import org.glavo.viewer.file.types.java.classfile.datatype.U1;
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
public class MethodParametersAttribute extends AttributeInfo {
    MethodParametersAttribute(CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength,
                              U1 parametersCount, Table<ParameterInfo> parameters) {
        super(attributeNameIndex, attributeLength);

        parametersCount.setName("parameters_count");
        parameters.setName("parameters");

        //noinspection unchecked
        this.getChildren().setAll(attributeNameIndex, attributeLength, parametersCount, parameters);
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
