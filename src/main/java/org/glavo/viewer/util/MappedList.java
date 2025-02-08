/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 Glavo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.glavo.viewer.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.transformation.TransformationList;

// https://gist.github.com/mikehearn/a2e4a048a996fd900656
public class MappedList<E, F> extends TransformationList<E, F> {
    private final Function<F, E> mapper;
    private final ArrayList<E> mapped;

    public MappedList(ObservableList<? extends F> source, Function<F, E> mapper) {
        super(source);
        this.mapper = mapper;
        this.mapped = new ArrayList<>(source.size());
        for (F val : getSource()) {
            mapped.add(mapper.apply(val));
        }
    }

    @Override
    protected void sourceChanged(ListChangeListener.Change<? extends F> c) {
        beginChange();
        while (c.next()) {
            if (c.wasPermutated()) {
                int[] perm = new int[c.getTo() - c.getFrom()];
                for (int i = c.getFrom(); i < c.getTo(); i++)
                    perm[i - c.getFrom()] = c.getPermutation(i);
                nextPermutation(c.getFrom(), c.getTo(), perm);
            } else if (c.wasUpdated()) {
                for (int i = c.getFrom(); i < c.getTo(); i++) {
                    remapIndex(i);
                    nextUpdate(i);
                }
            } else {
                if (c.wasRemoved()) {
                    List<E> removed = mapped.subList(c.getFrom(), c.getFrom() + c.getRemovedSize());
                    ArrayList<E> duped = new ArrayList<>(removed);
                    removed.clear();
                    nextRemove(c.getFrom(), duped);
                }
                if (c.wasAdded()) {
                    for (int i = c.getFrom(); i < c.getTo(); i++) {
                        mapped.addAll(c.getFrom(), c.getAddedSubList().stream().map(mapper).toList());
                        remapIndex(i);
                    }
                    nextAdd(c.getFrom(), c.getTo());
                }
            }
        }
        endChange();
    }

    private void remapIndex(int i) {
        if (i >= mapped.size()) {
            for (int j = mapped.size(); j <= i; j++) {
                mapped.add(mapper.apply(getSource().get(j)));
            }
        }
        mapped.set(i, mapper.apply(getSource().get(i)));
    }

    @Override
    public int getSourceIndex(int index) {
        return index;
    }

    @Override
    public int getViewIndex(int index) {
        return index;
    }

    @Override
    public E get(int index) {
        return mapped.get(index);
    }

    @Override
    public int size() {
        return mapped.size();
    }
}
