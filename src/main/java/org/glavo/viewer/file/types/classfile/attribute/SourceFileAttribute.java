package org.glavo.viewer.file.types.classfile.attribute;

import javafx.scene.image.ImageView;
import org.glavo.viewer.file.types.classfile.ClassFile;
import org.glavo.viewer.file.types.classfile.constant.ConstantPool;

/*
SourceFile_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u2 sourcefile_index;
}
 */
public final class SourceFileAttribute extends AttributeInfo {

    {
        u2cp("source_file_index");
    }

    @Override
    protected void postRead(ConstantPool cp) {
        setGraphic(new ImageView(ClassFile.ICON_ATTRIBUTE_SOURCE_FILE));
    }
}
