package org.glavo.viewer.file.types.java.classfile.attribute;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import kala.function.CheckedTriFunction;
import org.glavo.viewer.file.types.java.classfile.ClassFileComponent;
import org.glavo.viewer.file.types.java.classfile.ClassFileParseException;
import org.glavo.viewer.file.types.java.classfile.ClassFileReader;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantUtf8Info;
import org.glavo.viewer.file.types.java.classfile.datatype.*;
import org.glavo.viewer.resources.Images;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class AttributeInfo extends ClassFileComponent {
    static Image loadImage(String name) {
        return Images.loadImage("classfile/attribute/" + name);
    }

    private static final class Hole {
        static final Map<String, CheckedTriFunction<ClassFileReader, CpIndex<ConstantUtf8Info>, U4, AttributeInfo, IOException>> readers = new HashMap<>();

        static {
            readers.put("ConstantValue", ConstantValueAttribute::readFrom);
            readers.put("Code", CodeAttribute::readFrom);
            readers.put("StackMapTable", StackMapTableAttribute::readFrom);
            readers.put("Exceptions", ExceptionsAttribute::readFrom);
            readers.put("InnerClasses", InnerClassesAttribute::readFrom);
            readers.put("EnclosingMethod", EnclosingMethodAttribute::readFrom);
            readers.put("Synthetic", SyntheticAttribute::readFrom);
            readers.put("Signature", SignatureAttribute::readFrom);
            readers.put("SourceFile", SourceFileAttribute::readFrom);
            readers.put("SourceDebugExtension", SourceDebugExtensionAttribute::readFrom);
            readers.put("LineNumberTable", LineNumberTableAttribute::readFrom);
            readers.put("LocalVariableTable", LocalVariableTableAttribute::readFrom);
            readers.put("LocalVariableTypeTable", LocalVariableTypeTableAttribute::readFrom);
            readers.put("Deprecated", DeprecatedAttribute::readFrom);

            readers.put("RuntimeVisibleAnnotations", RuntimeAnnotationsAttribute::readFrom);
            readers.put("RuntimeInvisibleAnnotations", RuntimeAnnotationsAttribute::readFrom);

            readers.put("RuntimeVisibleParameterAnnotations", RuntimeParameterAnnotationsAttribute::readFrom);
            readers.put("RuntimeInvisibleParameterAnnotations", RuntimeParameterAnnotationsAttribute::readFrom);

            readers.put("RuntimeVisibleTypeAnnotations", RuntimeTypeAnnotationsAttribute::readFrom);
            readers.put("RuntimeInvisibleTypeAnnotations", RuntimeTypeAnnotationsAttribute::readFrom);

            readers.put("AnnotationDefault", AnnotationDefaultAttribute::readFrom);
            readers.put("BootstrapMethods", BootstrapMethodsAttribute::readFrom);
            readers.put("MethodParameters", MethodParametersAttribute::readFrom);
            readers.put("Module", ModuleAttribute::readFrom);
            readers.put("ModulePackages", ModulePackagesAttribute::readFrom);
            readers.put("ModuleMainClass", ModuleMainClassAttribute::readFrom);
            readers.put("NestHost", NestHostAttribute::readFrom);
            readers.put("NestMembers", NestMembersAttribute::readFrom);
            readers.put("Record", RecordAttribute::readFrom);
        }
    }

    public static AttributeInfo readFrom(ClassFileReader reader) throws IOException {
        int offset = reader.getOffset();

        CpIndex<ConstantUtf8Info> attributeNameIndex = reader.readCpIndexEager(ConstantUtf8Info.class);
        U4 attributeLength = reader.readU4();

        AttributeInfo res = Hole.readers.getOrDefault(attributeNameIndex.getConstantInfo().getDescText(), UndefinedAttribute::readFrom)
                .apply(reader, attributeNameIndex, attributeLength);

        res.setLength(reader.getOffset() - offset);

        if (res.getLength() - 6 != attributeLength.getIntValue())
            throw new ClassFileParseException("attributeLength(%s) != %s (offset=%s, attributeName=%s)"
                    .formatted(res.getLength() - 6, attributeLength.getIntValue(), Integer.toHexString(offset), attributeNameIndex.getConstantInfo().getDescText()));

        return res;
    }

    public static final Image image = loadImage("attribute.png");

    AttributeInfo(CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength) {
        attributeNameIndex.setName("attribute_name_index");
        attributeLength.setName("attribute_length");

        //noinspection unchecked
        this.getChildren().addAll(attributeNameIndex, attributeLength);

        this.setName(attributeNameIndex.getConstantInfo() == null ? null : attributeNameIndex.getConstantInfo().getDescText());
        this.setIcon(new ImageView(getImage()));
    }


    public Image getImage() {
        return image;
    }
}
