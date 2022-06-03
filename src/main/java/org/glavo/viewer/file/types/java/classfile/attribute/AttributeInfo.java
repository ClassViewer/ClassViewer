package org.glavo.viewer.file.types.java.classfile.attribute;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.glavo.viewer.file.types.java.classfile.ClassFileComponent;
import org.glavo.viewer.file.types.java.classfile.ClassFileParseException;
import org.glavo.viewer.file.types.java.classfile.ClassFileReader;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantClassInfo;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantNameAndTypeInfo;
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
            case "ConstantValue" ->
                    new ConstantValueAttribute(attributeNameIndex, attributeLength, reader.readCpIndexEager(ConstantValueInfo.class));
            case "Code" -> {
                U2 maxStack = reader.readU2();
                U2 maxLocals = reader.readU2();
                U4 codeLength = reader.readU4();
                CodeAttribute.Code code = CodeAttribute.Code.readFrom(reader, codeLength);
                U2 exceptionTableLength = reader.readU2();
                Table<CodeAttribute.ExceptionTableEntry> exceptionTable = Table.readFrom(reader, exceptionTableLength, CodeAttribute.ExceptionTableEntry::readFrom);
                U2 attributesCount = reader.readU2();
                Table<AttributeInfo> attributes = Table.readFrom(reader, attributesCount, AttributeInfo::readFrom);

                yield new CodeAttribute(attributeNameIndex, attributeLength,
                        maxStack, maxLocals,
                        codeLength, code,
                        exceptionTableLength, exceptionTable,
                        attributesCount, attributes);
            }
            case "StackMapTable" ->
                    new StackMapTableAttribute(attributeNameIndex, attributeLength, new Bytes(reader.readNBytes(attributeLength.getIntValue())));
            case "Exceptions" -> {
                U2 numberOfExceptions = reader.readU2();
                Table<CpIndex<ConstantClassInfo>> exceptionIndexTable = Table.readFrom(reader, numberOfExceptions, it -> it.readCpIndex(ConstantClassInfo.class));

                yield new ExceptionsAttribute(attributeNameIndex, attributeLength, numberOfExceptions, exceptionIndexTable);
            }
            case "InnerClasses" -> {
                U2 numberOfClasses = reader.readU2();
                Table<InnerClassesAttribute.InnerClassInfo> classes = Table.readFrom(reader, numberOfClasses, InnerClassesAttribute.InnerClassInfo::readFrom);

                yield new InnerClassesAttribute(attributeNameIndex, attributeLength, numberOfClasses, classes);
            }
            case "EnclosingMethod" ->
                    new EnclosingMethodAttribute(attributeNameIndex, attributeLength, reader.readCpIndexEager(ConstantClassInfo.class), reader.readCpIndexEager(ConstantNameAndTypeInfo.class));
            case "Synthetic" -> new SyntheticAttribute(attributeNameIndex, attributeLength);
            case "Signature" ->
                    new SignatureAttribute(attributeNameIndex, attributeLength, reader.readCpIndexEager(ConstantUtf8Info.class));
            case "SourceFile" ->
                    new SourceFileAttribute(attributeNameIndex, attributeLength, reader.readCpIndexEager(ConstantUtf8Info.class));
            case "SourceDebugExtension" ->
                    new SourceDebugExtensionAttribute(attributeNameIndex, attributeLength, new Bytes(reader.readNBytes(attributeLength.getIntValue())));
            case "LineNumberTable" -> {
                U2 lineNumberTableLength = reader.readU2();
                var lineNumberTable = Table.readFrom(reader, lineNumberTableLength, LineNumberTableAttribute.LineNumberTableEntry::readFrom);

                yield new LineNumberTableAttribute(attributeNameIndex, attributeLength, lineNumberTableLength, lineNumberTable);
            }
            case "LocalVariableTable" -> {
                U2 localVariableTableLength = reader.readU2();
                var localVariableTable = Table.readFrom(reader, localVariableTableLength, LocalVariableTableAttribute.LocalVariableTableEntry::readFrom);

                yield new LocalVariableTableAttribute(attributeNameIndex, attributeLength, localVariableTableLength, localVariableTable);
            }
            case "LocalVariableTypeTable" -> {
                U2 localVariableTypeTableLength = reader.readU2();
                var localVariableTypeTable = Table.readFrom(reader, localVariableTypeTableLength, LocalVariableTypeTableAttribute.LocalVariableTypeTableEntry::readFrom);

                yield new LocalVariableTypeTableAttribute(attributeNameIndex, attributeLength, localVariableTypeTableLength, localVariableTypeTable);
            }
            case "Deprecated" ->
                    new DeprecatedAttribute(attributeNameIndex, attributeLength);
            case "RuntimeVisibleAnnotations" -> {
                U2 annotationsCount = reader.readU2();
                var annotations = Table.readFrom(reader, annotationsCount, RuntimeVisibleAnnotationsAttribute.Annotation::readFrom, true);

                yield new RuntimeVisibleAnnotationsAttribute(attributeNameIndex, attributeLength, annotationsCount, annotations);
            }
            default ->
                    new UndefinedAttribute(attributeNameIndex, attributeLength, new Bytes(reader.readNBytes(attributeLength.getIntValue())));
        };

        res.setLength(reader.getOffset() - offset);

        assertAttributeLength(attributeLength.getIntValue(), res.getLength() - 6);
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
