package org.glavo.viewer.file.types.java.classfile.attribute;

import org.glavo.viewer.file.types.java.classfile.ClassFileComponent;
import org.glavo.viewer.file.types.java.classfile.ClassFileReader;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantUtf8Info;
import org.glavo.viewer.file.types.java.classfile.datatype.CpIndex;
import org.glavo.viewer.file.types.java.classfile.datatype.Table;
import org.glavo.viewer.file.types.java.classfile.datatype.U2;
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
    LocalVariableTableAttribute(CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength,
                                       U2 localVariableTableLength, Table<LocalVariableTableEntry> localVariableTable) {
        super(attributeNameIndex, attributeLength);

        localVariableTableLength.setName("local_variable_table_length");
        localVariableTable.setName("local_variable_table");

        //noinspection unchecked
        this.getChildren().setAll(attributeNameIndex, attributeLength, localVariableTableLength, localVariableTable);
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
