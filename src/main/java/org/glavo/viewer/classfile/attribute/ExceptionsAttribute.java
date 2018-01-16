package org.glavo.viewer.classfile.attribute;


import javafx.scene.image.ImageView;
import org.glavo.viewer.classfile.constant.ConstantPool;
import org.glavo.viewer.classfile.datatype.U2CpIndex;
import org.glavo.viewer.util.ImageUtils;

/*
Exceptions_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u2 number_of_exceptions;
    u2 exception_index_table[number_of_exceptions];
}
 */
public final class ExceptionsAttribute extends AttributeInfo {

    {
        u2("number_of_exceptions");
        table("exception_index_table", U2CpIndex.class);
    }

    @Override
    protected void postRead(ConstantPool cp) {
        setGraphic(new ImageView(ImageUtils.exceptionImage));
    }
}
