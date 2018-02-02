package org.glavo.viewer.classfile;

import org.glavo.viewer.classfile.constant.ConstantPool;
import org.glavo.viewer.FileComponent;
import org.glavo.viewer.FileParser;

public class ClassFileParser implements FileParser {
    
    public ClassFile parse(byte[] data) {
        ClassFile cf = new ClassFile();
        cf.read(new ClassFileReader(data));
        postRead(cf, cf.getConstantPool());
        return cf;
    }

    private static void postRead(ClassFileComponent fc, ConstantPool cp) {
        for (FileComponent c : fc.getComponents()) {
            postRead((ClassFileComponent) c, cp);
        }
        fc.postRead(cp);
    }

}
