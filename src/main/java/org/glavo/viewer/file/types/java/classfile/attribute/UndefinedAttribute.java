package org.glavo.viewer.file.types.java.classfile.attribute;

import javafx.scene.image.Image;
import org.glavo.viewer.file.types.java.classfile.ClassFileReader;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantUtf8Info;
import org.glavo.viewer.file.types.java.classfile.datatype.Bytes;
import org.glavo.viewer.file.types.java.classfile.datatype.CpIndex;
import org.glavo.viewer.file.types.java.classfile.datatype.U4;

import java.io.IOException;

/*
attribute_info {
    u2 attribute_name_index;
    u4 attribute_length;
    u1 info[attribute_length];
}
 */
public final class UndefinedAttribute extends AttributeInfo {
    static final Image image = AttributeInfo.loadImage("unknown.png");

    public static UndefinedAttribute readFrom(ClassFileReader reader, CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength) throws IOException {
        var attribute = new UndefinedAttribute(attributeNameIndex, attributeLength);
        attribute.readBytes(reader, "info", attributeLength);
        return attribute;
    }

    private UndefinedAttribute(CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength) {
        super(attributeNameIndex, attributeLength);
    }

    @Override
    public Image getImage() {
        return image;
    }
}
