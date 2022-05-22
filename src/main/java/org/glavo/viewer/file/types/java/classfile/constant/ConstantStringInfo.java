package org.glavo.viewer.file.types.java.classfile.constant;

import javafx.scene.control.Label;
import org.glavo.viewer.file.types.java.classfile.datatype.CpIndex;
import org.glavo.viewer.util.StringUtils;
import org.reactfx.value.Val;

/*
CONSTANT_String_info {
    u1 tag;
    u2 string_index;
}
*/
public final class ConstantStringInfo extends ConstantInfo {
    public ConstantStringInfo(ConstantInfo.Tag tag, CpIndex<ConstantUtf8Info> stringIndex) {
        super(tag);
        stringIndex.setName("string_index");

        //noinspection unchecked
        this.getChildren().setAll(tag, stringIndex);
        this.descProperty().bind(Val.map(stringIndex.constantInfoProperty(), it ->
                it == null ? null : new Label(StringUtils.cutAndAppendEllipsis(it.getText()))));
    }
}
