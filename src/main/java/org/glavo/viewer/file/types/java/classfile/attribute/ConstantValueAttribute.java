package org.glavo.viewer.file.types.java.classfile.attribute;

import javafx.scene.control.Label;
import org.glavo.viewer.file.types.java.classfile.ClassFileReader;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantUtf8Info;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantValueInfo;
import org.glavo.viewer.file.types.java.classfile.datatype.CpIndex;
import org.glavo.viewer.file.types.java.classfile.datatype.U4;
import org.glavo.viewer.util.StringUtils;
import org.reactfx.value.Val;

import java.io.IOException;

/*
ConstantValue_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u2 constantvalue_index;
}
 */
public final class ConstantValueAttribute extends AttributeInfo {
    public static ConstantValueAttribute readFrom(ClassFileReader reader, CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength) throws IOException {
        var attribute = new ConstantValueAttribute(attributeNameIndex, attributeLength);
        CpIndex<ConstantValueInfo> constantvalueIndex = attribute.readCpIndexEager(reader, "constantvalue_index", ConstantValueInfo.class);

        attribute.descProperty().bind(Val.flatMap(constantvalueIndex.constantInfoProperty(), it -> it == null ? null : it.descTextProperty())
                .map(it -> it == null ? null : new Label(StringUtils.cutAndAppendEllipsis(it))));

        return attribute;
    }

    private ConstantValueAttribute(CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength) {
        super(attributeNameIndex, attributeLength);
    }
}
