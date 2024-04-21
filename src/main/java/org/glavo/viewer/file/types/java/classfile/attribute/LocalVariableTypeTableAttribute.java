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
import org.glavo.viewer.file.types.java.classfile.ClassFileReader;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantUtf8Info;
import org.glavo.viewer.file.types.java.classfile.datatype.CpIndex;
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
public final class LocalVariableTypeTableAttribute extends AttributeInfo {
    public static LocalVariableTypeTableAttribute readFrom(ClassFileReader reader, CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength) throws IOException {
        var attribute = new LocalVariableTypeTableAttribute(attributeNameIndex, attributeLength);
        attribute.readU2TableLength(reader, "local_variable_type_table_length");
        attribute.readTable(reader, "local_variable_type_table", LocalVariableTypeTableAttribute.LocalVariableTypeTableEntry::readFrom);
        return attribute;
    }

    public LocalVariableTypeTableAttribute(CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength) {
        super(attributeNameIndex, attributeLength);
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
