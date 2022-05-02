package org.glavo.viewer.util;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class ReferenceCounter {
    private final AtomicInteger counter = new AtomicInteger();

    public void increment() {
        counter.incrementAndGet();
    }

    public void decrement() {
        if (counter.decrementAndGet() == 0) {
            cleanup();
        }
    }

    protected abstract void cleanup();
}
