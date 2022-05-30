package org.glavo.viewer.file.types.java.classfile.constant;

import javafx.beans.value.ObservableValue;
import org.glavo.viewer.file.types.java.classfile.datatype.U4;
import org.reactfx.value.Val;

/*
CONSTANT_Integer_info {
    u1 tag;
    u4 bytes;
}
*/
public final class ConstantIntegerInfo extends ConstantValueInfo {
    public ConstantIntegerInfo(ConstantInfo.Tag tag, U4 bytes) {
        super(tag);
        bytes.setName("bytes");

        this.getChildren().setAll(tag, bytes);
    }

    public U4 bytes() {
        return component(1);
    }

    @Override
    protected ObservableValue<String> initDescText() {
        return Val.map(bytes().intValueProperty(), it -> String.valueOf(it.intValue()));
    }
}
