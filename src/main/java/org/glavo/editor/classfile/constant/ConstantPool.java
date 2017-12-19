package org.glavo.editor.classfile.constant;

import org.glavo.editor.classfile.AbstractClassFileComponent;
import org.glavo.editor.classfile.ClassFileComponent;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public final class ConstantPool extends AbstractClassFileComponent implements List<ClassFileComponent> {
    public int size() {
        return components.size();
    }

    public boolean isEmpty() {
        return components.isEmpty();
    }

    public boolean contains(Object o) {
        return components.contains(o);
    }

    public Iterator<ClassFileComponent> iterator() {
        return components.iterator();
    }

    public Object[] toArray() {
        return components.toArray();
    }

    public <T> T[] toArray(T[] ts) {
        return components.toArray(ts);
    }

    public boolean add(ClassFileComponent classFileComponent) {
        return components.add(classFileComponent);
    }

    public boolean remove(Object o) {
        return components.remove(o);
    }

    public boolean containsAll(Collection<?> collection) {
        return components.containsAll(collection);
    }

    public boolean addAll(Collection<? extends ClassFileComponent> collection) {
        return components.addAll(collection);
    }

    public boolean addAll(int i, Collection<? extends ClassFileComponent> collection) {
        return components.addAll(i, collection);
    }

    public boolean removeAll(Collection<?> collection) {
        return components.removeAll(collection);
    }

    public boolean retainAll(Collection<?> collection) {
        return components.retainAll(collection);
    }

    public void replaceAll(UnaryOperator<ClassFileComponent> unaryOperator) {
        components.replaceAll(unaryOperator);
    }

    public void sort(Comparator<? super ClassFileComponent> comparator) {
        components.sort(comparator);
    }

    public void clear() {
        components.clear();
    }

    @Override
    public boolean equals(Object o) {
        return components.equals(o);
    }

    @Override
    public int hashCode() {
        return components.hashCode();
    }

    public ClassFileComponent get(int i) {
        return components.get(i);
    }

    public ClassFileComponent set(int i, ClassFileComponent classFileComponent) {
        return components.set(i, classFileComponent);
    }

    public void add(int i, ClassFileComponent classFileComponent) {
        components.add(i, classFileComponent);
    }

    public ClassFileComponent remove(int i) {
        return components.remove(i);
    }

    public int indexOf(Object o) {
        return components.indexOf(o);
    }

    public int lastIndexOf(Object o) {
        return components.lastIndexOf(o);
    }

    public ListIterator<ClassFileComponent> listIterator() {
        return components.listIterator();
    }

    public ListIterator<ClassFileComponent> listIterator(int i) {
        return components.listIterator(i);
    }

    public List<ClassFileComponent> subList(int i, int i1) {
        return components.subList(i, i1);
    }

    @Override
    public Spliterator<ClassFileComponent> spliterator() {
        return components.spliterator();
    }

    public boolean removeIf(Predicate<? super ClassFileComponent> predicate) {
        return components.removeIf(predicate);
    }

    public Stream<ClassFileComponent> stream() {
        return components.stream();
    }

    public Stream<ClassFileComponent> parallelStream() {
        return components.parallelStream();
    }

    @Override
    public void forEach(Consumer<? super ClassFileComponent> consumer) {
        components.forEach(consumer);
    }
}
