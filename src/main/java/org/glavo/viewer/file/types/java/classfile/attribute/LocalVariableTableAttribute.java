package org.glavo.viewer.file.types.java.classfile.attribute;

import org.glavo.viewer.file.types.java.classfile.ClassFileComponent;
import org.glavo.viewer.file.types.java.classfile.ClassFileReader;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantUtf8Info;
import org.glavo.viewer.file.types.java.classfile.datatype.CpIndex;
import org.glavo.viewer.file.types.java.classfile.datatype.U4;

import java.io.IOException;

/*
LocalVariableTable_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u2 local_variable_table_length;
    {   u2 start_pc;
        u2 length;
        u2 name_index;
        u2 descriptor_index;
        u2 index;
    } local_variable_table[local_variable_table_length];
}
 */
public class LocalVariableTableAttribute extends AttributeInfo {
    public static LocalVariableTableAttribute readFrom(ClassFileReader reader, CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength) throws IOException {
        var attribute = new LocalVariableTableAttribute(attributeNameIndex, attributeLength);
        attribute.readU2TableLength(reader, "local_variable_table_length");
        attribute.readTable(reader, "local_variable_table", LocalVariableTableAttribute.LocalVariableTableEntry::readFrom);
        return attribute;
    }

    private LocalVariableTableAttribute(CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength) {
        super(attributeNameIndex, attributeLength);
    }

    public static final class LocalVariableTableEntry extends ClassFileComponent {
        public static LocalVariableTableEntry readFrom(ClassFileReader reader) throws IOException {
            LocalVariableTableEntry entry = new LocalVariableTableEntry();
            entry.readU2(reader, "start_pc");
            entry.readU2(reader, "length");
            entry.readU2(reader, "name_index");
            entry.readU2(reader, "descriptor_index");
            entry.readU2(reader, "index");
            return entry;
        }
    }
}
