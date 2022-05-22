package org.glavo.viewer.file.types.java.classfile.constant;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import org.glavo.viewer.file.types.java.classfile.datatype.CpIndex;
import org.glavo.viewer.file.types.java.classfile.jvm.type.JavaType;
import org.glavo.viewer.file.types.java.classfile.jvm.type.JavaTypes;
import org.reactfx.value.Val;

/*
CONSTANT_NameAndType_info {
    u1 tag;
    u2 name_index;
    u2 descriptor_index;
}
*/
public final class ConstantNameAndTypeInfo extends ConstantInfo {
    private final ObjectProperty<JavaType> type = new SimpleObjectProperty<>();

    public ConstantNameAndTypeInfo(ConstantInfo.Tag tag, CpIndex<ConstantUtf8Info> nameIndex, CpIndex<ConstantUtf8Info> descriptorIndex) {
        super(tag);
        nameIndex.setName("name_index");
        descriptorIndex.setName("descriptor_index");

        //noinspection unchecked
        this.getChildren().setAll(tag, nameIndex, descriptorIndex);
        this.type.bind(Val.map(descriptorIndex.constantInfoProperty(), descriptor -> {
            if (descriptor == null || descriptor.getText() == null) return null;
            try {
                return JavaTypes.parseDescriptor(descriptor.getText());
            } catch (IllegalArgumentException ignored) {
                return null;
            }
        }));
        this.descProperty().bind(Val.combine(nameIndex.constantInfoProperty(), this.typeProperty(),
                (name, type) -> {
                    if (name == null || name.getText() == null || type == null) return null;

                    String text = type.isMethodType() ? name.getText() + type.getQualified() : name.getText() + ": " + type.getQualified();
                    Label label = new Label(text);
                    label.setTooltip(new Tooltip(text));
                    return label;
                }));
    }

    public ReadOnlyObjectProperty<JavaType> typeProperty() {
        return type;
    }

    public JavaType getType() {
        return type.get();
    }
}
