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
public final class CodeAttribute extends AttributeInfo {
    public static CodeAttribute readFrom(ClassFileReader reader, CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength) throws IOException {
        var attribute = new CodeAttribute(attributeNameIndex, attributeLength);

        attribute.readU2(reader, "max_stack");
        attribute.readU2(reader, "max_locals");

        U4 codeLength = attribute.readU4(reader, "code_length");
        attribute.read(reader, "code", it -> CodeAttribute.Code.readFrom(it, codeLength));

        attribute.readU2TableLength(reader, "exception_table_length");
        attribute.readTable(reader, "exception_table", CodeAttribute.ExceptionTableEntry::readFrom);

        attribute.readU2TableLength(reader, "attributes_count");
        attribute.readTable(reader, "attributes", AttributeInfo::readFrom);

        return attribute;
    }

    private CodeAttribute(CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength) {
        super(attributeNameIndex, attributeLength);
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
