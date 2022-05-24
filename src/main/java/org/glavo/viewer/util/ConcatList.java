package org.glavo.viewer.util;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.collections.WeakListChangeListener;

import java.util.List;

@SuppressWarnings("unchecked")
public class ConcatList<E> extends ObservableListBase<E> {
    private final List<ObservableList<? extends E>> lists;

    @SuppressWarnings("FieldCanBeLocal")
    private final ListChangeListener<E> listener;

    public ConcatList(ObservableList<? extends E>... lists) {
        this.lists = List.of(lists);

        listener = change -> this.fireChange(new Change(change));

        WeakListChangeListener<E> l = new WeakListChangeListener<>(listener);
        for (ObservableList<? extends E> list : lists) {
            list.addListener(l);
        }
    }

    private int getOffset(List<? extends E> list) {
        int offset = 0;

        for (ObservableList<? extends E> l : this.lists) {
            if (l == list) return offset;

            offset += list.size();
        }

        throw new AssertionError();
    }

    public List<ObservableList<? extends E>> getLists() {
        return lists;
    }

    @Override
    public E get(int index) {
        if (index < 0) throw new IndexOutOfBoundsException(index);

        int idx = index;
        for (ObservableList<? extends E> list : this.lists) {
            int listSize = list.size();
            if (idx < listSize) return list.get(idx);

            idx -= listSize;
        }

        throw new IndexOutOfBoundsException(index);
    }

    @Override
    public int size() {
        int size = 0;

        for (ObservableList<? extends E> list : lists) {
            size += list.size();
        }
        return size;
    }

    private final class Change extends ListChangeListener.Change<E> {
        private final ListChangeListener.Change<E> source;
        private final int offset;

        public Change(ListChangeListener.Change<? extends E> source) {
            super(ConcatList.this);
            this.source = (ListChangeListener.Change<E>) source;
            this.offset = getOffset(source.getList());
        }

        @Override
        public boolean next() {
            return source.next();
        }

        @Override
        public void reset() {
            source.reset();
        }

        @Override
        public int getFrom() {
            return source.getFrom() + offset;
        }

        @Override
        public int getTo() {
            return source.getTo() + offset;
        }

        @Override
        public List<E> getRemoved() {
            return source.getRemoved();
        }

        @Override
        protected int[] getPermutation() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getPermutation(int i) {
            return source.getPermutation(i - offset);
        }
    }
}
