package org.glavo.viewer.file.types.java.classfile;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.glavo.viewer.file.types.java.classfile.attribute.AttributeInfo;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantUtf8Info;
import org.glavo.viewer.file.types.java.classfile.datatype.AccessFlags;
import org.glavo.viewer.file.types.java.classfile.datatype.CpIndex;
import org.glavo.viewer.file.types.java.classfile.datatype.Table;
import org.glavo.viewer.file.types.java.classfile.datatype.U2;
import org.glavo.viewer.file.types.java.classfile.jvm.AccessFlag;
import org.glavo.viewer.file.types.java.classfile.jvm.AccessFlagType;
import org.glavo.viewer.file.types.java.classfile.jvm.type.JavaType;
import org.glavo.viewer.file.types.java.classfile.jvm.type.JavaTypes;
import org.reactfx.value.Val;

import java.io.IOException;
import java.util.ArrayDeque;

/*
field_info {
    u2             access_flags;
    u2             name_index;
    u2             descriptor_index;
    u2             attributes_count;
    attribute_info attributes[attributes_count];
}
 */
public class FieldInfo extends ClassFileComponent {

    public static FieldInfo readFrom(ClassFileReader reader) throws IOException {
        FieldInfo info = new FieldInfo();
        AccessFlags accessFlags = info.readAccessFlags(reader, "access_flags", AccessFlagType.AF_FIELD);
        CpIndex<ConstantUtf8Info> nameIndex = info.readCpIndex(reader, "name_index", ConstantUtf8Info.class);
        CpIndex<ConstantUtf8Info> descriptorIndex = info.readCpIndex(reader, "descriptor_index", ConstantUtf8Info.class);

        U2 attributesCount = info.readU2(reader, "attributes_count");
        Table<AttributeInfo> attributes = info.readTable(reader, "attributes", attributesCount, AttributeInfo::readFrom);


        info.nameProperty().bind(Val.map(nameIndex.constantInfoProperty(), it -> {
            if (it == null) return null;
            return it.getDescText();
        }));
        info.descProperty().bind(Val.map(descriptorIndex.constantInfoProperty(), it -> {
            if (it == null || it.getDescText() == null) return null;

            try {
                JavaType type = JavaTypes.parseDescriptor(it.getDescText());
                return new Label(type.getQualified());
            } catch (Throwable ignored) {
                return null;
            }
        }));
        info.iconProperty().bind(Val.map(accessFlags.flagsProperty(), flags -> {
            Node view = new ImageView(ClassFile.fieldImage);
            ArrayDeque<String> descriptors = new ArrayDeque<>(3);
            HBox box = new HBox();

            descriptors.add("field");

            if (flags.contains(AccessFlag.ACC_FINAL) && !flags.contains(AccessFlag.ACC_ENUM)) {
                view = new Group(view, new ImageView(ClassFile.finalMark));
                descriptors.addFirst("final");
            }

            if (flags.contains(AccessFlag.ACC_STATIC)) {
                view = new Group(view, new ImageView(ClassFile.staticMark));
                descriptors.addFirst("static");
            }
            box.getChildren().add(view);

            if (flags.contains(AccessFlag.ACC_PUBLIC)) {
                box.getChildren().add(new ImageView(ClassFile.publicImage));
                descriptors.addFirst("public");
            } else if (flags.contains(AccessFlag.ACC_PROTECTED)) {
                box.getChildren().add(new ImageView(ClassFile.protectedImage));
                descriptors.addFirst("protected");
            } else if (flags.contains(AccessFlag.ACC_PRIVATE)) {
                box.getChildren().add(new ImageView(ClassFile.privateImage));
                descriptors.addFirst("private");
            } else {
                box.getChildren().add(new ImageView(ClassFile.plocalImage));
                descriptors.addFirst("package local");
            }

            Tooltip.install(box, new Tooltip(String.join(" ", descriptors)));
            return box;
        }));
        return info;
    }
}
