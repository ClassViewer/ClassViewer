package org.glavo.viewer.file.types.java.classfile;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import kala.function.CheckedTriFunction;
import org.glavo.viewer.file.types.java.classfile.attribute.Attribute;
import org.glavo.viewer.file.types.java.classfile.attribute.UndefinedAttribute;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantUtf8Info;
import org.glavo.viewer.file.types.java.classfile.datatype.Bytes;
import org.glavo.viewer.file.types.java.classfile.datatype.CpIndex;
import org.glavo.viewer.file.types.java.classfile.datatype.U4;
import org.reactfx.value.Val;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import static org.glavo.viewer.util.Logging.LOGGER;

/*
attribute_info {
    u2 attribute_name_index;
    u4 attribute_length;
    u1 info[attribute_length];
}
 */
public class AttributeInfo extends ClassFileComponent {
    public static AttributeInfo readFrom(ClassFileReader reader) throws IOException {
        CpIndex<ConstantUtf8Info> attributeNameIndex = reader.readCpIndex(ConstantUtf8Info.class);
        U4 attributeLength = reader.readU4();
        byte[] info = reader.readNBytes(attributeLength.getIntValue());

        attributeNameIndex.loadDesc(reader.getClassFile().getView());

        return new AttributeInfo(attributeNameIndex, attributeLength, new Bytes(info));
    }

    private static final Map<String, CheckedTriFunction<CpIndex<ConstantUtf8Info>, U4, Bytes, Attribute, ?>> map = new HashMap<>();

    private final ObjectProperty<Attribute> attribute = new SimpleObjectProperty<>();

    public AttributeInfo(CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength, Bytes info) {

        attributeNameIndex.setName("attribute_name_index");
        attributeLength.setName("attribute_length");
        info.setName("info");

        attribute.addListener((o, oldValue, newValue) -> {
            this.nameProperty().unbind();
            this.descProperty().unbind();
            this.iconProperty().unbind();
            if (oldValue != null) Bindings.unbindContent(this.getChildren(), oldValue.getChildren());

            this.nameProperty().bind(newValue.nameProperty());
            this.descProperty().bind(newValue.descProperty());
            this.iconProperty().bind(newValue.iconProperty());
            Bindings.bindContent(this.getChildren(), newValue.getChildren());
        });
        attribute.bind(Val.combine(attributeNameIndex.constantInfoProperty(), info.valuesProperty(),
                (constant, bytes) -> {
                    try {
                        CheckedTriFunction<CpIndex<ConstantUtf8Info>, U4, Bytes, Attribute, ?> fun;
                        if (constant != null && constant.getDescText() != null && (fun = map.get(constant.getDescText())) != null) {
                            return fun.applyChecked(attributeNameIndex, attributeLength, info);
                        }
                    } catch (Throwable e) {
                        LOGGER.log(Level.WARNING, "Failed to parse attribute", e);
                    }

                    return new UndefinedAttribute(attributeNameIndex, attributeLength, info);
                }));
        //noinspection unchecked
        this.getChildren().setAll(attributeNameIndex, attributeLength, info);
    }


    public Attribute getAttribute() {
        return attribute.get();
    }
}
