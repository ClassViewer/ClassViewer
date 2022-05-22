package org.glavo.viewer.file.types.java.classfile.constant;

import javafx.scene.control.Label;
import org.glavo.viewer.file.types.java.classfile.datatype.CpIndex;
import org.glavo.viewer.util.StringUtils;
import org.reactfx.value.Val;

/*
CONSTANT_Class_info {
    u1 tag;
    u2 name_index;
}
*/
public final class ConstantClassInfo extends ConstantInfo {
    public ConstantClassInfo(ConstantInfo.Tag tag, CpIndex<ConstantUtf8Info> nameIndex) {
        super(tag);
        nameIndex.setName("name_index");

        //noinspection unchecked
        this.getChildren().setAll(tag, nameIndex);
        this.descProperty().bind(Val.map(nameIndex.constantInfoProperty(), it ->
                it == null ? null : new Label(StringUtils.cutAndAppendEllipsis(it.getText()))));
    }
}
