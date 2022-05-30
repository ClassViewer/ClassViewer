package org.glavo.viewer.file.types.java.classfile.attribute;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.glavo.viewer.file.types.java.classfile.ClassFileComponent;
import org.glavo.viewer.file.types.java.classfile.ClassFileParseException;
import org.glavo.viewer.file.types.java.classfile.ClassFileReader;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantUtf8Info;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantValueInfo;
import org.glavo.viewer.file.types.java.classfile.datatype.Bytes;
import org.glavo.viewer.file.types.java.classfile.datatype.CpIndex;
import org.glavo.viewer.file.types.java.classfile.datatype.U4;
import org.glavo.viewer.file.types.java.classfile.datatype.UInt;
import org.glavo.viewer.resources.Images;

import java.io.IOException;

public abstract class Attribute extends ClassFileComponent {
    static Image loadImage(String name) {
        return Images.loadImage("classfile/attribute/" + name);
    }

    private static void assertAttributeLength(int expected, UInt length) {
        if (length.getIntValue() != expected)
            throw new ClassFileParseException("attributeLength(%s) != %s".formatted(length, expected));
    }

    public static Attribute readFrom(ClassFileReader reader) throws IOException {
        int offset = reader.getOffset();

        CpIndex<ConstantUtf8Info> attributeNameIndex = reader.readCpIndexEager(ConstantUtf8Info.class);
        U4 attributeLength = reader.readU4();

        Attribute res = null;

        try {
            switch (attributeNameIndex.getConstantInfo().getDescText()) {
                case "ConstantValue" -> {
                    assertAttributeLength(2, attributeLength);
                    res = new ConstantValueAttribute(attributeNameIndex, attributeLength, reader.readCpIndexEager(ConstantValueInfo.class));
                }
            }
        } catch (Throwable ignored) {
        }

        if (res == null)
            res = new UndefinedAttribute(attributeNameIndex, attributeLength, new Bytes(reader.readNBytes(attributeLength.getIntValue())));

        res.setLength(reader.getOffset() - offset);
        return res;
    }

    //public static final Image image = loadImage("attribute.png");

    Attribute(CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength) {
        attributeNameIndex.setName("attribute_name_index");
        attributeLength.setName("attribute_length");

        this.setName(attributeNameIndex.getConstantInfo() == null ? null : attributeNameIndex.getConstantInfo().getDescText());
        this.setIcon(new ImageView(getImage()));
    }


    public Image getImage() {
        return UndefinedAttribute.image; // TODO
    }
}
