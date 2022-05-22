package org.glavo.viewer.file.types.java.classfile.constant;

import javafx.beans.binding.Bindings;
import javafx.scene.control.Label;
import org.glavo.viewer.file.types.java.classfile.ClassFile;
import org.glavo.viewer.file.types.java.classfile.datatype.U4;

/*
CONSTANT_Integer_info {
    u1 tag;
    u4 bytes;
}
*/
public final class ConstantIntegerInfo extends ConstantInfo {
    public ConstantIntegerInfo(ConstantInfo.Tag tag, U4 bytes) {
        super(tag);
        bytes.setName("bytes");

        this.getChildren().setAll(tag, bytes);
    }

    public U4 getBytes() {
        return (U4) getChildren().get(1);
    }

    @Override
    public void loadDesc(ClassFile classFile, ConstantPool constantPool) {
        this.descProperty().bind(Bindings.createObjectBinding(() -> new Label(String.valueOf(getBytes().getIntValue())),
                getBytes().intValueProperty()));
    }
}
