package org.glavo.viewer.file.types.java.classfile.attribute;

import org.glavo.viewer.file.types.java.classfile.constant.ConstantClassInfo;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantUtf8Info;
import org.glavo.viewer.file.types.java.classfile.datatype.CpIndex;
import org.glavo.viewer.file.types.java.classfile.datatype.Table;
import org.glavo.viewer.file.types.java.classfile.datatype.U2;
import org.glavo.viewer.file.types.java.classfile.datatype.U4;

/*
Exceptions_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u2 number_of_exceptions;
    u2 exception_index_table[number_of_exceptions];
}
 */
public class ExceptionsAttribute extends AttributeInfo {
    ExceptionsAttribute(CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength,
                        U2 numberOfExceptions, Table<CpIndex<ConstantClassInfo>> exceptionIndexTable) {
        super(attributeNameIndex, attributeLength);

        numberOfExceptions.setName("number_of_exceptions");
        exceptionIndexTable.setName("exception_index_table");

        //noinspection unchecked
        this.getChildren().setAll(attributeNameIndex, attributeLength, numberOfExceptions, exceptionIndexTable);
    }
}
