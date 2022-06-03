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
LocalVariableTypeTable_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u2 local_variable_type_table_length;
    {   u2 start_pc;
        u2 length;
        u2 name_index;
        u2 signature_index;
        u2 index;
    } local_variable_type_table[local_variable_type_table_length];
}
 */
public class LocalVariableTypeTableAttribute extends AttributeInfo {
    public LocalVariableTypeTableAttribute(CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength,
                                           U2 localVariableTypeTableLength, Table<LocalVariableTypeTableEntry> localVariableTypeTable) {
        super(attributeNameIndex, attributeLength);

        localVariableTypeTableLength.setName("local_variable_type_table_length");
        localVariableTypeTable.setName("local_variable_type_table");

        //noinspection unchecked
        this.getChildren().setAll(attributeNameIndex, attributeLength, localVariableTypeTableLength, localVariableTypeTable);
    }

    public static final class LocalVariableTypeTableEntry extends ClassFileComponent {
        public static LocalVariableTypeTableEntry readFrom(ClassFileReader reader) throws IOException {
            LocalVariableTypeTableEntry entry = new LocalVariableTypeTableEntry();
            entry.readU2(reader, "start_pc");
            entry.readU2(reader, "length");
            entry.readU2(reader, "name_index");
            entry.readU2(reader, "signature_index");
            entry.readU2(reader, "index");
            return entry;
        }
    }
}
