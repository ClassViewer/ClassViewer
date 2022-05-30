package org.glavo.viewer.file.types.java.classfile.attribute;

import org.glavo.viewer.file.types.java.classfile.ClassFileComponent;
import org.glavo.viewer.file.types.java.classfile.ClassFileReader;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantClassInfo;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantUtf8Info;
import org.glavo.viewer.file.types.java.classfile.datatype.*;

import java.io.IOException;

/*
Code_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u2 max_stack;
    u2 max_locals;
    u4 code_length;
    u1 code[code_length];
    u2 exception_table_length;
    {   u2 start_pc;
        u2 end_pc;
        u2 handler_pc;
        u2 catch_type;
    } exception_table[exception_table_length];
    u2 attributes_count;
    attribute_info attributes[attributes_count];
}
 */
public class CodeAttribute extends AttributeInfo {
    CodeAttribute(CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength,
                  U2 maxStack, U2 maxLocals,
                  U4 codeLength, Code code,
                  U2 exceptionTableLength, Table<ExceptionTableEntry> exceptionTable,
                  U2 attributesCount, Table<AttributeInfo> attributes
    ) {
        super(attributeNameIndex, attributeLength);
        maxStack.setName("max_stack");
        maxLocals.setName("max_locals");
        codeLength.setName("code_length");
        code.setName("code");
        exceptionTableLength.setName("exception_table_length");
        exceptionTable.setName("exception_table");
        attributesCount.setName("attributes_count");
        attributes.setName("attributes");
    }

    public static final class Code extends ClassFileComponent {
        public static Code readFrom(ClassFileReader reader, UInt codeLength) throws IOException {
            reader.readNBytes(codeLength.getIntValue()); // TODO: Parse byte code

            Code res = new Code();
            res.setLength(codeLength.getIntValue());
            return res;
        }
    }

    public static final class ExceptionTableEntry extends ClassFileComponent {
        public static ExceptionTableEntry readFrom(ClassFileReader reader) throws IOException {
            return new ExceptionTableEntry(reader.readU2(), reader.readU2(), reader.readU2(), reader.readCpIndex(ConstantClassInfo.class));
        }

        public ExceptionTableEntry(U2 startPC, U2 endPC, U2 handlerPC, CpIndex<ConstantClassInfo> catchType) {
            startPC.setName("start_pc");
            endPC.setName("end_pc");
            handlerPC.setName("handler_pc");
            catchType.setName("catch_type");

            this.setLength(8);
        }
    }
}
