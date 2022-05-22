package org.glavo.viewer.file.types.java.classfile.constant;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import org.glavo.viewer.file.types.java.classfile.datatype.Bytes;
import org.glavo.viewer.file.types.java.classfile.datatype.U2;
import org.glavo.viewer.file.types.java.classfile.jvm.Mutf8Decoder;
import org.glavo.viewer.util.StringUtils;

/*
CONSTANT_Utf8_info {
    u1 tag;
    u2 length;
    u1 bytes[length];
}
*/
public final class ConstantUtf8Info extends ConstantInfo {
    private final StringProperty text = new SimpleStringProperty();

    public ConstantUtf8Info(ConstantInfo.Tag tag, U2 length, Bytes bytes) {
        super(tag);
        length.setName("length");
        bytes.setName("bytes");

        this.getChildren().setAll(tag, length, bytes);

        String str = Mutf8Decoder.decodeMutf8(bytes.getValues());
        text.set(str);
        Label label = new Label(StringUtils.cutAndAppendEllipsis(str));
        label.setTooltip(new Tooltip(str));
        this.setDesc(label);
    }

    public Bytes getBytes() {
        return (Bytes) getChildren().get(2);
    }

    public ReadOnlyStringProperty textProperty() {
        return text;
    }

    public String getText() {
        return text.get();
    }
}
