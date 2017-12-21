package org.glavo.viewer.classfile.attribute;


import org.glavo.viewer.classfile.ClassFileComponent;
import org.glavo.viewer.classfile.constant.ConstantPool;

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

    {
        u2   ("line_number_table_length");
        table("line_number_table", LineNumberTableEntry.class);
    }

    
    public static class LineNumberTableEntry extends ClassFileComponent {

        {
            u2("start_pc");
            u2("line_number");
        }

        @Override
        protected void postRead(ConstantPool cp) {
            int lineNumber = super.getUInt("line_number");
            int startPc = super.getUInt("start_pc");
            setName("line " + lineNumber);
            setDesc(Integer.toString(startPc));
        }

    }
    
}
