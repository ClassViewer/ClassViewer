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
LineNumberTable_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u2 line_number_table_length;
    {   u2 start_pc;
        u2 line_number;
    } line_number_table[line_number_table_length];
}
 */
public class LineNumberTableAttribute extends AttributeInfo {
    LineNumberTableAttribute(CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength,
                             U2 lineNumberTableLength, Table<LineNumberTableEntry> lineNumberTable) {
        super(attributeNameIndex, attributeLength);

        lineNumberTableLength.setName("line_number_table_length");
        lineNumberTable.setName("line_number_table");

        //noinspection unchecked
        this.getChildren().setAll(attributeNameIndex, attributeLength, lineNumberTableLength, lineNumberTable);
    }

    public static final class LineNumberTableEntry extends ClassFileComponent {
        public static LineNumberTableEntry readFrom(ClassFileReader reader) throws IOException {
            LineNumberTableEntry entry = new LineNumberTableEntry();
            entry.readU2(reader, "start_pc");
            entry.readU2(reader, "line_number");
            return entry;
        }
    }
}
