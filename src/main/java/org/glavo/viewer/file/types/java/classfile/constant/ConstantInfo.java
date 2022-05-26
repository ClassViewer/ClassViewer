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
import java.util.HashMap;
import java.util.Map;

public sealed abstract class ConstantInfo extends ClassFileComponent
        permits ConstantClassInfo,
        ConstantRefInfo,
        ConstantStringInfo, ConstantIntegerInfo, ConstantFloatInfo,
        ConstantLongInfo, ConstantDoubleInfo,
        ConstantNameAndTypeInfo,
        ConstantUtf8Info,
        ConstantMethodHandleInfo, ConstantMethodTypeInfo,
        ConstantInvokeDynamicInfo,
        ConstantModuleInfo, ConstantPackageInfo {
    //@formatter:off
    public static final int CONSTANT_Class              = 7;  // C
    public static final int CONSTANT_Fieldref           = 9;  // F
    public static final int CONSTANT_Methodref          = 10; // M
    public static final int CONSTANT_InterfaceMethodref = 11; // I
    public static final int CONSTANT_String             = 8;  // S
    public static final int CONSTANT_Integer            = 3;  // I
    public static final int CONSTANT_Float              = 4;  // F
    public static final int CONSTANT_Long               = 5;  // L
    public static final int CONSTANT_Double             = 6;  // D
    public static final int CONSTANT_NameAndType        = 12; // N
    public static final int CONSTANT_Utf8               = 1;  // T
    public static final int CONSTANT_MethodHandle       = 15; // H
    public static final int CONSTANT_MethodTypeInfo     = 16; // T
    public static final int CONSTANT_InvokeDynamic      = 18; // D
    public static final int CONSTANT_Module         = 19; // M
    public static final int CONSTANT_Package        = 20; // P
    //@formatter:on

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
            case CONSTANT_MethodTypeInfo -> new ConstantMethodTypeInfo(tag, reader.readCpIndex(ConstantUtf8Info.class));
            case CONSTANT_InvokeDynamic ->
                    new ConstantInvokeDynamicInfo(tag, reader.readU2(), reader.readCpIndex(ConstantNameAndTypeInfo.class));
            case CONSTANT_Module -> new ConstantModuleInfo(tag, reader.readCpIndex(ConstantUtf8Info.class));
            case CONSTANT_Package -> new ConstantPackageInfo(tag, reader.readCpIndex(ConstantUtf8Info.class));
            default -> throw new ClassFileParseException("Unknown constant tag: " + tag.contentToString());
        };
        info.setLength(reader.getOffset() - offset);
        return info;
    }

    private static final Map<String, Image> images = new HashMap<>();

    public ConstantInfo(ConstantInfo.Tag tag) {
        String name = this.getConstantName();
        tag.setTagName(name);
        ImageView view = new ImageView(images.computeIfAbsent(name, key -> Images.loadImage("classfile/constant/" + key + ".png")));
        Tooltip.install(view, new Tooltip(name));
        this.setIcon(view);
    }

    public Tag tag() {
        return component(0);
    }

    private static final int PREFIX_LENGTH = "CONSTANT".length();
    private static final int SUFFIX_LENGTH = "Info".length();

    public String getConstantName() {
        String simpleName = this.getClass().getSimpleName();
        return simpleName.substring(PREFIX_LENGTH, simpleName.length() - SUFFIX_LENGTH);
    }

    public int getIndex() {
        if (!(this.getParent().getValue() instanceof ConstantPool pool)) throw new AssertionError();

        int idx = pool.getConstants().indexOf(this);
        assert idx > 0;
        return idx;
    }

    private ObservableValue<String> descText;

    protected abstract ObservableValue<String> initDescText();

    @Override
    public void loadDesc(ClassFileTreeView view) {
        this.descProperty().bind(Val.map(descTextProperty(), text -> {
            if (text == null) return null;

            Label label = new Label(StringUtils.cutAndAppendEllipsis(text));
            label.setTooltip(new Tooltip(text));
            return label;
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
            this.setDesc(new Label("CONSTANT_" + tagName + "(" + intValue + ")"));
        }
    }
}
