package org.glavo.editor.classfile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractClassFileComponent extends ClassFileComponent {
    protected List<ClassFileComponent> components = new ArrayList<>();

    public List<ClassFileComponent> getComponents() {
        return Collections.unmodifiableList(components);
    }

    @Override
    public Iterator<ClassFileComponent> iterator() {
        return components.iterator();
    }
}
