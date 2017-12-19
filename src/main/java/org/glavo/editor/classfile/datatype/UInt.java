package org.glavo.editor.classfile.datatype;

import lombok.Getter;
import org.glavo.editor.classfile.ClassFileComponent;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class UInt extends ClassFileComponent {
    @Getter
    protected int value;



    @Override
    public Iterator<ClassFileComponent> iterator() {
        return new Iterator<ClassFileComponent>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public ClassFileComponent next() {
                throw new NoSuchElementException();
            }
        };
    }
}
