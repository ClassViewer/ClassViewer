package org.glavo.viewer.file.types.java.classfile.constant;

import javafx.scene.image.Image;
import org.glavo.viewer.file.types.java.classfile.datatype.U1;
import org.glavo.viewer.file.types.java.classfile.datatype.U2;
import org.glavo.viewer.resources.Images;

/*
CONSTANT_Class_info {
    u1 tag;
    u2 name_index;
}
*/
public final class ConstantClassInfo extends ConstantInfo {
    private static final Image image = Images.loadImage("classfile/constant/class.png");

    public ConstantClassInfo(U1 tag, U2 nameIndex) {
        super(tag);
        nameIndex.setName("name_index");

        this.getChildren().setAll(tag, nameIndex);
    }

    @Override
    public Image getImage() {
        return image;
    }
}
