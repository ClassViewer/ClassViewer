package org.glavo.viewer.file.types.java.classfile.constant;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import org.glavo.viewer.file.types.java.classfile.datatype.Bytes;
import org.glavo.viewer.file.types.java.classfile.datatype.U2;
import org.glavo.viewer.file.types.java.classfile.jvm.Mutf8Decoder;

/*
CONSTANT_Utf8_info {
    u1 tag;
    u2 length;
    u1 bytes[length];
}
*/
public final class ConstantUtf8Info extends ConstantInfo {
    public ConstantUtf8Info(ConstantInfo.Tag tag, U2 length, Bytes bytes) {
        super(tag);
        length.setName("length");
        bytes.setName("bytes");

        this.getChildren().setAll(tag, length, bytes);
    }

    public Bytes bytes() {
        return (Bytes) getChildren().get(2);
    }

    @Override
    protected ObservableValue<String> initDescText() {
        return Bindings.createStringBinding(() -> Mutf8Decoder.decodeMutf8(bytes().getValues()));
    }
}
