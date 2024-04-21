/*
 * Copyright 2024 Glavo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.glavo.viewer.file.types.java.classfile;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import kala.collection.base.Iterators;
import kala.value.primitive.IntVar;
import org.glavo.viewer.file.types.java.classfile.attribute.AttributeInfo;
import org.glavo.viewer.file.types.java.classfile.attribute.RecordAttribute;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantClassInfo;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantPool;
import org.glavo.viewer.file.types.java.classfile.datatype.*;
import org.glavo.viewer.file.types.java.classfile.jvm.AccessFlag;
import org.glavo.viewer.file.types.java.classfile.jvm.AccessFlagType;
import org.glavo.viewer.resources.Images;
import org.reactfx.value.Val;

import java.io.IOException;
import java.util.ArrayDeque;

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
public final class ClassFile extends ClassFileComponent {
    public static final Image classImage = Images.loadImage("classfile/class.png");
    public static final Image abstractClassImage = Images.loadImage("classfile/abstractClass.png");
    public static final Image interfaceImage = Images.loadImage("classfile/interface.png");
    public static final Image annotationImage = Images.loadImage("classfile/annotation.png");
    public static final Image enumImage = Images.loadImage("classfile/enum.png");
    public static final Image recordImage = Images.loadImage("classfile/record.png");

    public static final Image fieldImage = Images.loadImage("classfile/field.png");
    public static final Image methodImage = Images.loadImage("classfile/method.png");
    public static final Image abstractMethodImage = Images.loadImage("classfile/abstractMethod.png");

    public static final Image publicImage = Images.loadImage("classfile/public.png");
    public static final Image protectedImage = Images.loadImage("classfile/protected.png");
    public static final Image plocalImage = Images.loadImage("classfile/plocal.png");
    public static final Image privateImage = Images.loadImage("classfile/private.png");

    public static final Image finalMark = Images.loadImage("classfile/finalMark.png");
    public static final Image staticMark = Images.loadImage("classfile/staticMark.png");
    public static final Image runnableMark = Images.loadImage("classfile/runnableMark.png");


    private final ClassFileTreeView view;

    ClassFile(ClassFileTreeView view) {
        this.view = view;
    }

    public static ClassFile readFrom(ClassFileTreeView v, ClassFileReader reader) throws IOException {
        ClassFile classFile = new ClassFile(v);
        reader.classFile = classFile;
        v.setRoot(classFile);

        U4Hex magic = classFile.readU4Hex(reader, "magic");
        if (magic.getIntValue() != 0xCAFEBABE) throw new ClassFileParseException("magic number mismatch: " + magic);

        U2 minorVersion = classFile.readU2(reader, "minor_version");
        U2 majorVersion = classFile.readU2(reader, "major_version");

        {
            StringBinding javaSEVersion = Bindings.createStringBinding(() -> {
                final String unknownVersion = "Java SE ???";

                int minor = minorVersion.getIntValue();
                int major = majorVersion.getIntValue();

                if (major < 45) {
                    return unknownVersion;
                }

                int versionNumber = major - 44;

                if (major >= 56) {
                    if (minor == 0)
                        return "Java SE " + versionNumber;
                    else if (minor == 0xFFFF)
                        return "Java SE " + versionNumber + " (Preview)";
                    else
                        return unknownVersion;
                }

                if (major >= 50) { // >= Java SE 6
                    if (minor == 0)
                        return "Java SE " + versionNumber;
                    else
                        return "Java SE " + versionNumber + "." + minor;
                }

                if (major >= 46) {
                    if (minor == 0)
                        return "Java SE 1." + versionNumber;
                    else
                        return "Java SE 1." + versionNumber + "." + minor;
                }

                //noinspection ConstantValue
                if (major == 45 && minor == 3)
                    return "Java SE 1.1";

                return unknownVersion;
            }, minorVersion.intValueProperty(), majorVersion.intValueProperty());

            Tooltip tooltip = new Tooltip();
            tooltip.textProperty().bind(javaSEVersion);
            minorVersion.setTooltip(tooltip);
            majorVersion.setTooltip(tooltip);
        }


        U2 cpCount = classFile.readU2(reader, "constant_pool_count");
        ConstantPool constantPool = ConstantPool.readFrom(reader, cpCount);
        classFile.getChildren().add(constantPool);
        classFile.setLength(reader.getOffset() - classFile.getOffset());

        AccessFlags accessFlags = classFile.readAccessFlags(reader, "access_flags", AccessFlagType.AF_CLASS);
        CpIndex<ConstantClassInfo> thisClass = classFile.readCpIndex(reader, "this_class", ConstantClassInfo.class);
        CpIndex<ConstantClassInfo> superClass = classFile.readCpIndex(reader, "super_class", ConstantClassInfo.class);

        U2 interfacesCount = classFile.readU2(reader, "interfaces_count");
        Table<CpIndex<ConstantClassInfo>> interfaces = classFile.readTable(reader, "interfaces", interfacesCount, it -> it.readCpIndex(ConstantClassInfo.class));

        U2 fieldsCount = classFile.readU2(reader, "fields_count");
        Table<FieldInfo> fields = classFile.readTable(reader, "fields", fieldsCount, FieldInfo::readFrom);

        U2 methodsCount = classFile.readU2(reader, "methods_count");
        Table<MethodInfo> methods = classFile.readTable(reader, "methods", methodsCount, MethodInfo::readFrom);

        U2 attributesCount = classFile.readU2(reader, "attributes_count");
        Table<AttributeInfo> attributes = classFile.readTable(reader, "attributes", attributesCount, AttributeInfo::readFrom);

        classFile.calculateOffset(new IntVar());
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
            } else if (Iterators.anyMatch(attributes.getChildren().iterator(), it -> it instanceof RecordAttribute)) {
                view = new ImageView(recordImage);
                descriptors.addFirst("record");
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

    public ClassFileTreeView getView() {
        return view;
    }

    public ConstantPool getConstantPool() {
        return component(4);
    }
}
