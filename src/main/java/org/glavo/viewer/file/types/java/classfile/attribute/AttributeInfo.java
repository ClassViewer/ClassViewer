package org.glavo.viewer.file.types.java.classfile.attribute;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.glavo.viewer.file.types.java.classfile.ClassFileComponent;
import org.glavo.viewer.file.types.java.classfile.ClassFileParseException;
import org.glavo.viewer.file.types.java.classfile.ClassFileReader;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantUtf8Info;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantValueInfo;
import org.glavo.viewer.file.types.java.classfile.datatype.*;
import org.glavo.viewer.resources.Images;

import java.io.IOException;

public abstract class AttributeInfo extends ClassFileComponent {
    static Image loadImage(String name) {
        return Images.loadImage("classfile/attribute/" + name);
    }

    private static void assertAttributeLength(int expected, int actual) {
        if (actual != expected)
            throw new ClassFileParseException("attributeLength(%s) != %s".formatted(actual, expected));
    }

    public static AttributeInfo readFrom(ClassFileReader reader) throws IOException {
        int offset = reader.getOffset();

        CpIndex<ConstantUtf8Info> attributeNameIndex = reader.readCpIndexEager(ConstantUtf8Info.class);
        U4 attributeLength = reader.readU4();

        AttributeInfo res = switch (attributeNameIndex.getConstantInfo().getDescText()) {
            case "ConstantValue" -> {
                assertAttributeLength(2, attributeLength.getIntValue());
                yield new ConstantValueAttribute(attributeNameIndex, attributeLength, reader.readCpIndexEager(ConstantValueInfo.class));
            }
            case "Code" -> {
                U2 maxStack = reader.readU2();
                U2 maxLocals = reader.readU2();
                U4 codeLength = reader.readU4();
                CodeAttribute.Code code = CodeAttribute.Code.readFrom(reader, codeLength);
                U2 exceptionTableLength = reader.readU2();
                Table<CodeAttribute.ExceptionTableEntry> exceptionTable = Table.readFrom(reader, exceptionTableLength, CodeAttribute.ExceptionTableEntry::readFrom);
                U2 attributesCount = reader.readU2();
                Table<AttributeInfo> attributes = Table.readFrom(reader, attributesCount, AttributeInfo::readFrom);

                assertAttributeLength(attributeLength.getIntValue(), reader.getOffset() - offset - 6);

                yield new CodeAttribute(attributeNameIndex, attributeLength,
                        maxStack, maxLocals,
                        codeLength, code,
                        exceptionTableLength, exceptionTable,
                        attributesCount, attributes);
            }

            default ->
                    new UndefinedAttribute(attributeNameIndex, attributeLength, new Bytes(reader.readNBytes(attributeLength.getIntValue())));
        };

        res.setLength(reader.getOffset() - offset);
        return res;
    }

    public static final Image image = loadImage("attribute.png");

    AttributeInfo(CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength) {
        attributeNameIndex.setName("attribute_name_index");
        attributeLength.setName("attribute_length");

        this.setName(attributeNameIndex.getConstantInfo() == null ? null : attributeNameIndex.getConstantInfo().getDescText());
        this.setIcon(new ImageView(getImage()));
    }


    public Image getImage() {
        return image;
    }
}
