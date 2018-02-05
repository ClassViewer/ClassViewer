package org.glavo.viewer.classfile;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.glavo.viewer.classfile.attribute.AttributeInfo;
import org.glavo.viewer.classfile.constant.ConstantPool;
import org.glavo.viewer.classfile.datatype.U2AccessFlags;
import org.glavo.viewer.classfile.jvm.AccessFlagType;
import org.glavo.viewer.util.ImageUtils;

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
        u2af("access_flags", AccessFlagType.AF_METHOD);
        u2cp("name_index");
        u2cp("descriptor_index");
        u2("attributes_count");
        table("attributes", AttributeInfo.class);
    }

    @Override
    protected void postRead(ConstantPool cp) {
        int nameIndex = super.getUInt("name_index");
        if (nameIndex > 0) {
            // todo fix loading java.lang.String from rt.jar
            setDesc(cp.getUtf8String(nameIndex));
        }
        U2AccessFlags acc = (U2AccessFlags) get("access_flags");

        if (acc != null) {
            HBox box = new HBox();
            Node view;

            if (acc.isAbstract()) {
                view = new ImageView(ImageUtils.abstractMethodImage);
            } else {
                view = new ImageView(ImageUtils.methodImage);
            }

            if (acc.isFinal()) {
                view = new Group(view, new ImageView(ImageUtils.finalMarkImage));
            } else if(acc.isStatic()) {
                view = new Group(view, new ImageView(ImageUtils.staticMarkImage));
            }

            box.getChildren().add(view);

            if (acc.isPrivate()) {
                box.getChildren().add(new ImageView(ImageUtils.privateImage));
            } else if (acc.isProtected()) {
                box.getChildren().add(new ImageView(ImageUtils.protectedImage));
            } else if (acc.isPublic()) {
                box.getChildren().add(new ImageView(ImageUtils.publicImage));
            } else {
                box.getChildren().add(new ImageView(ImageUtils.plocalImage));
            }

            setGraphic(box);
        }
    }

}
