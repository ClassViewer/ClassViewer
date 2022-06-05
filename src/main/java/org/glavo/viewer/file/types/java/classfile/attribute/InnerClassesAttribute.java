package org.glavo.viewer.file.types.java.classfile.attribute;

import org.glavo.viewer.file.types.java.classfile.ClassFileComponent;
import org.glavo.viewer.file.types.java.classfile.ClassFileReader;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantClassInfo;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantUtf8Info;
import org.glavo.viewer.file.types.java.classfile.datatype.CpIndex;
import org.glavo.viewer.file.types.java.classfile.datatype.Table;
import org.glavo.viewer.file.types.java.classfile.datatype.U2;
import org.glavo.viewer.file.types.java.classfile.datatype.U4;
import org.glavo.viewer.file.types.java.classfile.jvm.AccessFlagType;
import org.reactfx.value.Val;

import java.io.IOException;

/*
InnerClasses_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u2 number_of_classes;
    {   u2 inner_class_info_index;
        u2 outer_class_info_index;
        u2 inner_name_index;
        u2 inner_class_access_flags;
    } classes[number_of_classes];
}
 */
public final class InnerClassesAttribute extends AttributeInfo {
    public static InnerClassesAttribute readFrom(ClassFileReader reader, CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength) throws IOException {
        var attribute = new InnerClassesAttribute(attributeNameIndex, attributeLength);
        attribute.readU2TableLength(reader, "number_of_classes");
        attribute.readTable(reader, "classes", InnerClassInfo::readFrom, true);
        return attribute;
    }

    private InnerClassesAttribute(CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength) {
        super(attributeNameIndex, attributeLength);
    }

    public static final class InnerClassInfo extends ClassFileComponent {
        public static InnerClassInfo readFrom(ClassFileReader reader) throws IOException {
            InnerClassInfo info = new InnerClassInfo();
            info.readCpIndex(reader, "inner_class_info_index", ConstantClassInfo.class);
            info.readCpIndex(reader, "outer_class_info_index", ConstantClassInfo.class);
            CpIndex<ConstantUtf8Info> innerName = info.readCpIndex(reader, "inner_name_index", ConstantUtf8Info.class);
            info.readAccessFlags(reader, "inner_class_access_flags", AccessFlagType.AF_NESTED_CLASS);

            info.nameProperty().bind(Val.map(innerName.constantInfoProperty(), it -> it == null ? null : it.getDescText()));
            info.setLength(8);
            return info;
        }
    }
}
