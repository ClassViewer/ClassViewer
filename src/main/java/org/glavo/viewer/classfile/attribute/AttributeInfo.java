package org.glavo.viewer.classfile.attribute;


import javafx.scene.image.ImageView;
import org.glavo.viewer.classfile.ClassFileComponent;
import org.glavo.viewer.classfile.constant.ConstantPool;
import org.glavo.viewer.gui.support.FileType;
import org.glavo.viewer.gui.support.ImageUtils;

/*
attribute_info {
    u2 attribute_name_index;
    u4 attribute_length;
    u1 info[attribute_length];
}
 */
public abstract class AttributeInfo extends ClassFileComponent {

    {
        u2("attribute_name_index");
        u4("attribute_length");
    }

    @Override
    protected void postRead(ConstantPool cp) {
        setGraphic(new ImageView(ImageUtils.attributeImage));
    }
}
