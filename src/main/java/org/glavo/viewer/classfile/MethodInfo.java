package org.glavo.viewer.classfile;

import javafx.scene.image.ImageView;
import org.glavo.viewer.classfile.attribute.AttributeInfo;
import org.glavo.viewer.classfile.constant.ConstantPool;
import org.glavo.viewer.classfile.datatype.U2AccessFlags;
import org.glavo.viewer.classfile.jvm.AccessFlagType;
import org.glavo.viewer.classfile.jvm.AccessFlags;
import org.glavo.viewer.gui.support.ImageUtils;

/*
method_info {
    u2             access_flags;
    u2             name_index;
    u2             descriptor_index;
    u2             attributes_count;
    attribute_info attributes[attributes_count];
}
 */
public class MethodInfo extends ClassFileComponent {

    {
        u2af ("access_flags", AccessFlagType.AF_METHOD);
        u2cp ("name_index");
        u2cp ("descriptor_index");
        u2   ("attributes_count");
        table("attributes", AttributeInfo.class);
    }

    @Override
    protected void postRead(ConstantPool cp) {
        int nameIndex = super.getUInt("name_index");
        if (nameIndex > 0) {
            // todo fix loading java.lang.String from rt.jar
            setDesc(cp.getUtf8String(nameIndex));
        }
        U2AccessFlags flags = (U2AccessFlags) get("access_flags");

        if(flags.isAbstract()) {
            setGraphic(new ImageView(ImageUtils.abstractMethodImage));
        } else if (flags.isFinal()) {
            setGraphic(new ImageView(ImageUtils.methodImage)); //todo
        } else {
            setGraphic(new ImageView(ImageUtils.methodImage));
        }
    }
    
}
