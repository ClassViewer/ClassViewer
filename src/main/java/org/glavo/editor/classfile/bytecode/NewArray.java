package org.glavo.editor.classfile.bytecode;

import org.glavo.editor.classfile.constant.ConstantPool;
import org.glavo.editor.classfile.jvm.Opcode;
import org.glavo.editor.common.ParseException;

public class NewArray extends Instruction {

    {
        u1("opcode");
        u1("atype");
    }

    public NewArray(Opcode opcode, int pc) {
        super(opcode, pc);
    }
    
    @Override
    protected void postRead(ConstantPool cp) {
        int atype = super.getUInt("atype");
        setDesc(getDesc() + " " + getArrayType(atype));
    }
    
    private static String getArrayType(int atype) {
        switch (atype) {
            case  4: return "boolean";
            case  5: return "char";
            case  6: return "float";
            case  7: return "double";
            case  8: return "byte";
            case  9: return "short";
            case 10: return "int";
            case 11: return "long";
            default: throw new ParseException("Invalid atype: " + atype);
        }
    }
    
}
