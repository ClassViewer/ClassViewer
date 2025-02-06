package org.glavo.viewer.file.types.classfile.constant;


import org.glavo.viewer.file.types.classfile.ClassFileComponent;

/*
cp_info {
    u1 tag;
    u1 info[];
}
 */
public abstract class ConstantInfo extends ClassFileComponent {

    {
        u1("tag");
    }

    protected abstract String loadDesc(ConstantPool cp);
    
}
