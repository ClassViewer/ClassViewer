package org.glavo.editor.classfile.constant;


import org.glavo.editor.classfile.ClassFileComponent;

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
