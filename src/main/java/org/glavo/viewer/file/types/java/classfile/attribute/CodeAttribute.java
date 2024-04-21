/*
 * Copyright 2024 Glavo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.glavo.viewer.file.types.java.classfile.attribute;

import org.glavo.viewer.file.types.java.classfile.ClassFileComponent;
import org.glavo.viewer.file.types.java.classfile.ClassFileParseException;
import org.glavo.viewer.file.types.java.classfile.ClassFileReader;
import org.glavo.viewer.file.types.java.classfile.Instruction;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantClassInfo;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantUtf8Info;
import org.glavo.viewer.file.types.java.classfile.datatype.CpIndex;
import org.glavo.viewer.file.types.java.classfile.datatype.U4;
import org.glavo.viewer.file.types.java.classfile.datatype.UInt;

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
            Code res = new Code();
            int length = codeLength.getIntValue();
            int baseOffset = reader.getOffset();

            int pc;
            while ((pc = reader.getOffset() - baseOffset) < length) {
                res.getChildren().add(Instruction.readFrom(reader, pc));
            }

            if (pc != length) {
                throw new ClassFileParseException("code length mismatch");
            }

            res.setLength(length);
            return res;
        }
    }

    public static final class ExceptionTableEntry extends ClassFileComponent {
        public static ExceptionTableEntry readFrom(ClassFileReader reader) throws IOException {
            ExceptionTableEntry entry = new ExceptionTableEntry();
            entry.readU2(reader, "start_pc");
            entry.readU2(reader, "end_pc");
            entry.readU2(reader, "handler_pc");
            entry.readCpIndex(reader, "catch_type", ConstantClassInfo.class);

            return entry;
        }

    }
}
