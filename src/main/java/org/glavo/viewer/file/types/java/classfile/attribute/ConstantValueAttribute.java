package org.glavo.viewer.file.types.java.classfile.attribute;

import javafx.scene.control.Label;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantUtf8Info;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantValueInfo;
import org.glavo.viewer.file.types.java.classfile.datatype.CpIndex;
import org.glavo.viewer.file.types.java.classfile.datatype.U4;
import org.glavo.viewer.util.StringUtils;
import org.reactfx.value.Val;

/*
ConstantValue_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u2 constantvalue_index;
}
 */
public class ConstantValueAttribute extends Attribute {
    public ConstantValueAttribute(CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength, CpIndex<ConstantValueInfo> constantvalueIndex) {
        super(attributeNameIndex, attributeLength);

        constantvalueIndex.setName("constantvalue_index");

        this.descProperty().bind(Val.flatMap(constantvalueIndex.constantInfoProperty(), it -> it == null ? null : it.descTextProperty())
                .map(it -> it == null ? null : new Label(StringUtils.cutAndAppendEllipsis(it))));

        //noinspection unchecked
        this.getChildren().setAll(attributeNameIndex, attributeLength, constantvalueIndex);
    }
}
