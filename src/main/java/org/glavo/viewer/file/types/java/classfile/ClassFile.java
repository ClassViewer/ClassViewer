package org.glavo.viewer.file.types.java.classfile;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import kala.value.primitive.IntRef;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantClassInfo;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantPool;
import org.glavo.viewer.file.types.java.classfile.datatype.AccessFlags;
import org.glavo.viewer.file.types.java.classfile.datatype.CpIndex;
import org.glavo.viewer.file.types.java.classfile.datatype.U2;
import org.glavo.viewer.file.types.java.classfile.datatype.U4Hex;
import org.glavo.viewer.file.types.java.classfile.jvm.AccessFlag;
import org.glavo.viewer.file.types.java.classfile.jvm.AccessFlagType;
import org.glavo.viewer.resources.Images;
import org.reactfx.value.Val;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;

/*
ClassFile {
    u4             magic;
    u2             minor_version;
    u2             major_version;
    u2             constant_pool_count;
    cp_info        constant_pool[constant_pool_count-1];
    u2             access_flags;
    u2             this_class;
    u2             super_class;
    u2             interfaces_count;
    u2             interfaces[interfaces_count];
    u2             fields_count;
    field_info     fields[fields_count];
    u2             methods_count;
    method_info    methods[methods_count];
    u2             attributes_count;
    attribute_info attributes[attributes_count];
}
*/
public class ClassFile extends ClassFileComponent {
    public static final Image classImage = Images.loadImage("classfile/class.png");
    public static final Image abstractClassImage = Images.loadImage("classfile/abstractClass.png");
    public static final Image interfaceImage = Images.loadImage("classfile/interface.png");
    public static final Image annotationImage = Images.loadImage("classfile/annotation.png");
    public static final Image enumImage = Images.loadImage("classfile/enum.png");
    public static final Image recordImage = Images.loadImage("classfile/enum.png");

    public static final Image publicImage = Images.loadImage("classfile/public.png");
    public static final Image protectedImage = Images.loadImage("classfile/protected.png");
    public static final Image plocalImage = Images.loadImage("classfile/plocal.png");
    public static final Image privateImage = Images.loadImage("classfile/private.png");

    public static final Image finalMark = Images.loadImage("classfile/finalMark.png");
    public static final Image staticMark = Images.loadImage("classfile/staticMark.png");
    public static final Image runnableMark = Images.loadImage("classfile/runnableMark.png");


    public static ClassFile readFrom(ClassFileReader reader) throws IOException {
        ClassFile classFile = new ClassFile();

        U4Hex magic = classFile.readU4Hex(reader, "magic");
        if (magic.getIntValue() != 0xCAFEBABE) throw new ClassFileParseException("magic number mismatch: " + magic);

        classFile.readU2(reader, "minor_version");
        classFile.readU2(reader, "major_version");

        U2 cpCount = classFile.readU2(reader, "constant_pool_count");
        ConstantPool constantPool = ConstantPool.readFrom(reader, cpCount);
        classFile.getChildren().add(constantPool);
        classFile.setLength(reader.getOffset() - classFile.getOffset());

        AccessFlags accessFlags = classFile.readAccessFlags(reader, "access_flags", AccessFlagType.AF_CLASS);
        CpIndex<ConstantClassInfo> thisClass = classFile.readCpIndex(reader, "this_class", ConstantClassInfo.class);
        CpIndex<ConstantClassInfo> superClass = classFile.readCpIndex(reader, "super_class", ConstantClassInfo.class);

        classFile.calculateOffset(new IntRef());
        classFile.iconProperty().bind(Val.map(accessFlags.flagsProperty(), flags -> {
            HBox box = new HBox();
            ArrayDeque<String> descriptors = new ArrayDeque<>(4);

            Node view;
            if (flags.contains(AccessFlag.ACC_ANNOTATION)) {
                view = new ImageView(annotationImage);
                descriptors.addFirst("annotation type");
            } else if (flags.contains(AccessFlag.ACC_ENUM)) {
                view = new ImageView(enumImage);
                descriptors.addFirst("enum");
            } else if (flags.contains(AccessFlag.ACC_INTERFACE)) {
                view = new ImageView(interfaceImage);
                descriptors.addFirst("interface");
            } else if (flags.contains(AccessFlag.ACC_ABSTRACT)) {
                view = new ImageView(abstractClassImage);
                descriptors.addFirst("abstract class");
            } else {
                view = new ImageView(classImage);
                descriptors.addFirst("class");
            }

            if (flags.contains(AccessFlag.ACC_FINAL) && !flags.contains(AccessFlag.ACC_ENUM)) {
                view = new Group(view, new ImageView(finalMark));
                descriptors.addFirst("final");
            }

            if (flags.contains(AccessFlag.ACC_STATIC)) {
                view = new Group(view, new ImageView(staticMark));
                descriptors.addFirst("static");
            }
            box.getChildren().add(view);

            if (flags.contains(AccessFlag.ACC_PUBLIC)) {
                box.getChildren().add(new ImageView(publicImage));
                descriptors.addFirst("public");
            } else if (flags.contains(AccessFlag.ACC_PROTECTED)) {
                box.getChildren().add(new ImageView(protectedImage));
                descriptors.addFirst("protected");
            } else if (flags.contains(AccessFlag.ACC_PRIVATE)) {
                box.getChildren().add(new ImageView(privateImage));
                descriptors.addFirst("private");
            } else {
                box.getChildren().add(new ImageView(plocalImage));
                descriptors.addFirst("package local");
            }

            Tooltip.install(box, new Tooltip(String.join(" ", descriptors)));
            return box;
        }));
        classFile.nameProperty().bind(Val.map(thisClass.constantInfoProperty(), info -> {
            if (info == null || info.getDescText() == null) return null;
            String text = info.getDescText();
            int idx = text.lastIndexOf('/');
            if (idx >= 0) {
                text = text.substring(idx + 1);
            }
            return text;
        }));
        classFile.setExpanded(true);
        return classFile;
    }


    public ConstantPool getConstantPool() {
        return (ConstantPool) getChildren().get(4);
    }
}
