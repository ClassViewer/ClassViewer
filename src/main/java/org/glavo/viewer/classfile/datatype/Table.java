package org.glavo.viewer.classfile.datatype;

import org.glavo.viewer.classfile.ClassFileComponent;
import org.glavo.viewer.classfile.ClassFileReader;
import org.glavo.viewer.classfile.attribute.AttributeFactory;
import org.glavo.viewer.classfile.attribute.AttributeInfo;
import org.glavo.viewer.classfile.constant.ConstantPool;
import org.glavo.viewer.FileComponent;
import org.glavo.viewer.ParseException;
import org.glavo.viewer.util.StringUtils;

/**
 * Array of class components.
 */
public final class Table extends ClassFileComponent {

    private final UInt length;
    private final Class<? extends ClassFileComponent> entryClass;

    public Table(UInt length, Class<? extends ClassFileComponent> entryClass) {
        this.length = length;
        this.entryClass = entryClass;
    }
    
    @Override
    protected void readContent(ClassFileReader reader) {
        try {
            for (int i = 0; i < length.getIntValue(); i++) {
                super.add(readEntry(reader));
            }
        } catch (ReflectiveOperationException e) {
            throw new ParseException(e);
        }
    }

    private ClassFileComponent readEntry(ClassFileReader reader) throws ReflectiveOperationException {
        if (entryClass == AttributeInfo.class) {
            return readAttributeInfo(reader);
        } else {
            ClassFileComponent c = entryClass.newInstance();
            c.read(reader);
            return c;
        }
    }
    
    private AttributeInfo readAttributeInfo(ClassFileReader reader) {
        int attrNameIndex = reader.getShort(reader.getPosition());
        String attrName = reader.getConstantPool().getUtf8String(attrNameIndex);
        
        AttributeInfo attr = AttributeFactory.create(attrName);
        attr.setName(attrName);
        attr.read(reader);
        
        return attr;
    }

    @Override
    protected void postRead(ConstantPool cp) {
        int i = 0;
        for (FileComponent entry : super.getComponents()) {
            String newName = StringUtils.formatIndex(length.getIntValue(), i++);
            String oldName = entry.getName();
            if (oldName != null) {
                newName += " (" + oldName + ")";
            }
            entry.setName(newName);
        }
    }

}
