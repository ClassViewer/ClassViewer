package org.glavo.viewer.file.types.java.classfile.constant;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Background;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.glavo.viewer.file.types.java.classfile.ClassFile;
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
    public ConstantUtf8Info(ConstantInfo.Tag tag, U2 length, Bytes bytes) {
        super(tag);
        length.setName("length");
        bytes.setName("bytes");

        this.getChildren().setAll(tag, length, bytes);
    }

    public Bytes getBytes() {
        return (Bytes) getChildren().get(2);
    }

    @Override
    public void loadDesc(ClassFile classFile, ConstantPool constantPool) {
        String str = Mutf8Decoder.decodeMutf8(getBytes().getValues());
        Label label = new Label(StringUtils.cutAndAppendEllipsis(str));
        this.setDesc(label);
        label.setTooltip(new Tooltip(str));
    }
}
