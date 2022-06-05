package org.glavo.viewer.file.types.java.classfile.attribute;

import javafx.scene.control.Label;
import org.glavo.viewer.file.types.java.classfile.ClassFileComponent;
import org.glavo.viewer.file.types.java.classfile.ClassFileReader;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantUtf8Info;
import org.glavo.viewer.file.types.java.classfile.datatype.CpIndex;
import org.glavo.viewer.file.types.java.classfile.datatype.U4;
import org.glavo.viewer.file.types.java.classfile.jvm.type.JavaType;
import org.glavo.viewer.file.types.java.classfile.jvm.type.JavaTypes;
import org.reactfx.value.Val;

import java.io.IOException;

/*
Record_attribute {
    u2                    attribute_name_index;
    u4                    attribute_length;
    u2                    components_count;
    record_component_info components[components_count];
}
 */
public final class RecordAttribute extends AttributeInfo {
    public static RecordAttribute readFrom(ClassFileReader reader, CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength) throws IOException {
        var attribute = new RecordAttribute(attributeNameIndex, attributeLength);
        attribute.readU2TableLength(reader, "components_count");
        attribute.readTable(reader, "components", RecordComponentInfo::readFrom, true);
        return attribute;
    }

    private RecordAttribute(CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength) {
        super(attributeNameIndex, attributeLength);
    }

    /*
    record_component_info {
        u2             name_index;
        u2             descriptor_index;
        u2             attributes_count;
        attribute_info attributes[attributes_count];
    }
     */
    public static final class RecordComponentInfo extends ClassFileComponent {
        public static RecordComponentInfo readFrom(ClassFileReader reader) throws IOException {
            var component = new RecordComponentInfo();
            var nameIndex = component.readCpIndexEager(reader, "name_index", ConstantUtf8Info.class);
            var descriptorIndex = component.readCpIndexEager(reader, "descriptor_index", ConstantUtf8Info.class);
            component.readU2TableLength(reader, "attributes_count");
            component.readTable(reader, "attributes", AttributeInfo::readFrom);

            component.descProperty().bind(Val.combine(nameIndex.constantInfoProperty(), descriptorIndex.constantInfoProperty(),
                    (name, descriptor) -> {
                        if (name == null || name.getDescText() == null
                                || descriptor == null || descriptor.getDescText() == null) return null;

                        try {
                            JavaType type = JavaTypes.parseDescriptor(descriptor.getDescText());
                            if (!type.isMethodType()) return new Label(name.getDescText() + ": " + type.getQualified());
                        } catch (Throwable ignored) {
                        }
                        return null;
                    }));
            return component;
        }
    }
}
