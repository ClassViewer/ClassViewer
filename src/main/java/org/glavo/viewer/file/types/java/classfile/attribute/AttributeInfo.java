package org.glavo.viewer.file.types.java.classfile.attribute;

import org.glavo.viewer.file.types.java.classfile.ClassFileComponent;
import org.glavo.viewer.file.types.java.classfile.ClassFileReader;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantInfo;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantUtf8Info;
import org.glavo.viewer.file.types.java.classfile.datatype.Bytes;
import org.glavo.viewer.file.types.java.classfile.datatype.CpIndex;
import org.glavo.viewer.file.types.java.classfile.datatype.U4;
import org.reactfx.value.Val;

import java.io.IOException;

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

        return new AttributeInfo(attributeNameIndex, attributeLength, new Bytes(info));
    }

    public AttributeInfo(CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength, Bytes info) {
        attributeNameIndex.setName("attribute_name_index");
        attributeLength.setName("attribute_length");
        info.setName("info");

        //noinspection unchecked
        this.getChildren().setAll(attributeNameIndex, attributeLength, info);

        this.nameProperty().bind(Val.map(attributeNameIndex.constantInfoProperty(), ConstantInfo::getDescText));
    }
}
