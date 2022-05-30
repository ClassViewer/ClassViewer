package org.glavo.viewer.file.types.java.classfile.attribute;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.glavo.viewer.file.types.java.classfile.ClassFileComponent;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantUtf8Info;
import org.glavo.viewer.file.types.java.classfile.datatype.Bytes;
import org.glavo.viewer.file.types.java.classfile.datatype.CpIndex;
import org.glavo.viewer.file.types.java.classfile.datatype.U4;
import org.glavo.viewer.resources.Images;

public abstract class Attribute extends ClassFileComponent {
    static Image loadImage(String name) {
        return Images.loadImage("classfile/attribute/" + name);
    }

    //public static final Image image = loadImage("attribute.png");

    Attribute(CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength, Bytes info) {
        this.setName(attributeNameIndex.getConstantInfo() == null ? null : attributeNameIndex.getConstantInfo().getDescText());
        this.setIcon(new ImageView(getImage()));
    }

    public Image getImage() {
        return null; // TODO
    }
}
