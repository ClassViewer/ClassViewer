package org.glavo.viewer.file.types.java.classfile;

import javafx.scene.control.TreeItem;
import kala.value.primitive.IntRef;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantPool;
import org.glavo.viewer.file.types.java.classfile.datatype.U2;
import org.glavo.viewer.file.types.java.classfile.datatype.U4Hex;

import java.io.IOException;

public class ClassFile extends ClassFileComponent {

    public static ClassFile readFrom(ClassFileReader reader) throws IOException {
        ClassFile res = new ClassFile();
        res.setName("<In development>");

        U4Hex magic = res.readU4Hex(reader, "magic");
        if (magic.getIntValue() != 0xCAFEBABE) throw new ClassFileParseException("magic number mismatch: " + magic);

        res.readU2(reader, "minor_version");
        res.readU2(reader, "major_version");

        U2 cpCount = res.readU2(reader, "constant_pool_count");

        // TODO
        ConstantPool constantPool = ConstantPool.readFrom(reader, cpCount);
        res.getChildren().add(constantPool);
        res.setLength(reader.getOffset() - res.getOffset());

        res.calculateOffset(new IntRef());
        loadDesc(res, res, constantPool);
        return res;
    }

    private static void loadDesc(ClassFileComponent component, ClassFile classFile, ConstantPool constantPool) {
        component.loadDesc(classFile, constantPool);
        for (TreeItem<ClassFileComponent> child : component.getChildren()) {
            loadDesc(child.getValue(), classFile, constantPool);
        }
    }
}
