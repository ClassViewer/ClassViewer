package org.glavo.viewer.file.types.java.classfile.attribute;

import org.glavo.viewer.file.types.java.classfile.ClassFileComponent;
import org.glavo.viewer.file.types.java.classfile.ClassFileReader;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantUtf8Info;
import org.glavo.viewer.file.types.java.classfile.datatype.CpIndex;
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
public final class LineNumberTableAttribute extends AttributeInfo {
    public static LineNumberTableAttribute readFrom(ClassFileReader reader, CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength) throws IOException {
        var attribute = new LineNumberTableAttribute(attributeNameIndex, attributeLength);
        attribute.readU2TableLength(reader, "line_number_table_length");
        attribute.readTable(reader, "line_number_table", LineNumberTableAttribute.LineNumberTableEntry::readFrom);
        return attribute;
    }

    private LineNumberTableAttribute(CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength) {
        super(attributeNameIndex, attributeLength);
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
