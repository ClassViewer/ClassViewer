package org.glavo.viewer.file.types.java.classfile.constant;

import javafx.beans.binding.Bindings;
import javafx.scene.control.Label;
import org.glavo.viewer.file.types.java.classfile.datatype.U4;

/*
CONSTANT_Float_info {
    u1 tag;
    u4 bytes;
}
*/
public final class ConstantFloatInfo extends ConstantInfo {
    public ConstantFloatInfo(ConstantInfo.Tag tag, U4 bytes) {
        super(tag);
        bytes.setName("bytes");

        this.getChildren().setAll(tag, bytes);
        this.descProperty().bind(Bindings.createObjectBinding(() -> new Label(String.valueOf(Float.intBitsToFloat(bytes.getIntValue()))),
                bytes.intValueProperty()));
    }

    public U4 getBytes() {
        return (U4) getChildren().get(1);
    }
}
