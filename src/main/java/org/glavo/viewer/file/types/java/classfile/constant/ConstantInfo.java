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
package org.glavo.viewer.file.types.java.classfile.constant;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.glavo.viewer.file.types.java.classfile.ClassFileComponent;
import org.glavo.viewer.file.types.java.classfile.ClassFileParseException;
import org.glavo.viewer.file.types.java.classfile.ClassFileReader;
import org.glavo.viewer.file.types.java.classfile.ClassFileTreeView;
import org.glavo.viewer.file.types.java.classfile.datatype.Bytes;
import org.glavo.viewer.file.types.java.classfile.datatype.U2;
import org.glavo.viewer.resources.Images;
import org.glavo.viewer.util.StringUtils;
import org.reactfx.value.Val;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;

public sealed abstract class ConstantInfo extends ClassFileComponent
        permits ConstantClassInfo,
        ConstantRefInfo,
        ConstantValueInfo,
        ConstantNameAndTypeInfo,
        ConstantUtf8Info,
        ConstantMethodHandleInfo, ConstantMethodTypeInfo,
        ConstantInvokeDynamicInfo,
        ConstantModuleInfo, ConstantPackageInfo {
    //@formatter:off
    public static final int CONSTANT_Class              =  7;
    public static final int CONSTANT_Fieldref           =  9;
    public static final int CONSTANT_Methodref          = 10;
    public static final int CONSTANT_InterfaceMethodref = 11;
    public static final int CONSTANT_String             =  8;
    public static final int CONSTANT_Integer            =  3;
    public static final int CONSTANT_Float              =  4;
    public static final int CONSTANT_Long               =  5;
    public static final int CONSTANT_Double             =  6;
    public static final int CONSTANT_NameAndType        = 12;
    public static final int CONSTANT_Utf8               =  1;
    public static final int CONSTANT_MethodHandle       = 15;
    public static final int CONSTANT_MethodType         = 16;
    public static final int CONSTANT_InvokeDynamic      = 18;
    public static final int CONSTANT_Module             = 19;
    public static final int CONSTANT_Package            = 20;
    //@formatter:on

    private static final class Hole {
        static final HashMap<Class<?>, String> constantNameMap = new HashMap<>();
        static final HashMap<Class<?>, String> constantTagNameMap = new HashMap<>();
        static final HashMap<Class<?>, Image> images = new HashMap<>();

        static {
            final int prefixLength = "CONSTANT_".length();
            final String pkg = ConstantInfo.class.getPackageName();

            try {
                for (Field field : ConstantInfo.class.getFields()) {
                    if (field.getType() == int.class && field.getName().startsWith("CONSTANT_")) {
                        final String name = field.getName().substring(prefixLength).intern();
                        final int tagValue = field.getInt(null);
                        final Class<?> cls = Class.forName(pkg + ".Constant" + name + "Info");

                        constantNameMap.put(cls, name);
                        constantTagNameMap.put(cls, "CONSTANT_" + name + " (" + tagValue + ")");
                        images.put(cls, Images.loadImage("classfile/constant/" + name + ".png"));
                    }
                }
            } catch (Throwable e) {
                throw new AssertionError(e);
            }
        }
    }

    public static ConstantInfo readFrom(ClassFileReader reader) throws IOException {
        int offset = reader.getOffset();

        int tagValue = reader.readUnsignedByte();
        Tag tag = new Tag(tagValue);
        ConstantInfo info = switch (tagValue) {
            case CONSTANT_Class -> new ConstantClassInfo(tag, reader.readCpIndex(ConstantUtf8Info.class));
            case CONSTANT_Fieldref ->
                    new ConstantFieldrefInfo(tag, reader.readCpIndex(ConstantClassInfo.class), reader.readCpIndex(ConstantNameAndTypeInfo.class));
            case CONSTANT_Methodref ->
                    new ConstantMethodrefInfo(tag, reader.readCpIndex(ConstantClassInfo.class), reader.readCpIndex(ConstantNameAndTypeInfo.class));
            case CONSTANT_InterfaceMethodref ->
                    new ConstantInterfaceMethodrefInfo(tag, reader.readCpIndex(ConstantClassInfo.class), reader.readCpIndex(ConstantNameAndTypeInfo.class));
            case CONSTANT_String -> new ConstantStringInfo(tag, reader.readCpIndex(ConstantUtf8Info.class));
            case CONSTANT_Integer -> new ConstantIntegerInfo(tag, reader.readU4());
            case CONSTANT_Float -> new ConstantFloatInfo(tag, reader.readU4());
            case CONSTANT_Long -> new ConstantLongInfo(tag, reader.readU4(), reader.readU4());
            case CONSTANT_Double -> new ConstantDoubleInfo(tag, reader.readU4(), reader.readU4());
            case CONSTANT_NameAndType ->
                    new ConstantNameAndTypeInfo(tag, reader.readCpIndex(ConstantUtf8Info.class), reader.readCpIndex(ConstantUtf8Info.class));
            case CONSTANT_Utf8 -> {
                U2 length = reader.readU2();
                yield new ConstantUtf8Info(tag, length, new Bytes(reader.readNBytes(length.getIntValue())));
            }
            case CONSTANT_MethodHandle ->
                    new ConstantMethodHandleInfo(tag, reader.readU1(), reader.readCpIndex(ConstantInfo.class));
            case CONSTANT_MethodType -> new ConstantMethodTypeInfo(tag, reader.readCpIndex(ConstantUtf8Info.class));
            case CONSTANT_InvokeDynamic ->
                    new ConstantInvokeDynamicInfo(tag, reader.readU2(), reader.readCpIndex(ConstantNameAndTypeInfo.class));
            case CONSTANT_Module -> new ConstantModuleInfo(tag, reader.readCpIndex(ConstantUtf8Info.class));
            case CONSTANT_Package -> new ConstantPackageInfo(tag, reader.readCpIndex(ConstantUtf8Info.class));
            default -> throw new ClassFileParseException("Unknown constant tag: " + tag.contentToString());
        };
        info.setLength(reader.getOffset() - offset);
        return info;
    }


    public ConstantInfo(ConstantInfo.Tag tag) {
        String name = this.getConstantName();
        tag.setTagName(Hole.constantTagNameMap.get(this.getClass()));
        ImageView view = new ImageView(Hole.images.get(this.getClass()));
        Tooltip.install(view, new Tooltip(name));
        this.setIcon(view);
    }

    public Tag tag() {
        return component(0);
    }

    public String getConstantName() {
        return Hole.constantNameMap.get(this.getClass());
    }

    public int getIndex() {
        if (!(this.getParent().getValue() instanceof ConstantPool pool)) throw new AssertionError();

        int idx = pool.getConstants().indexOf(this);
        assert idx > 0;
        return idx;
    }

    @Override
    public String contentToString() {
        return getDescText();
    }

    private ObservableValue<String> descText;

    protected abstract ObservableValue<String> initDescText();

    @Override
    public void loadDesc(ClassFileTreeView view) {
        this.descProperty().bind(Val.map(descTextProperty(), text -> {
            if (text == null) return null;

            return StringUtils.cutTextNode(text, Label::new);
        }));
    }

    public ObservableValue<String> descTextProperty() {
        if (descText == null) {
            descText = initDescText();
        }
        return descText;
    }

    public String getDescText() {
        return descText.getValue();
    }

    public static final class Tag extends ClassFileComponent {
        private final int intValue;

        Tag(int value) {
            this.setLength(1);
            this.setName("tag");
            this.intValue = value;
        }

        public int getIntValue() {
            return intValue;
        }

        void setTagName(String tagName) {
            this.setDesc(new Label(tagName));
        }
    }
}
