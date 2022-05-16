package org.glavo.viewer.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class DaemonThreadFactory implements ThreadFactory {
    private final String namePrefix;

    public DaemonThreadFactory() {
        this("daemon-thread");
    }

    public DaemonThreadFactory(String namePrefix) {
        this.namePrefix = namePrefix;
    }

    private final AtomicInteger c = new AtomicInteger();

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setDaemon(true);
        thread.setName(namePrefix + "-" + c.getAndIncrement());
        return thread;
    }
}
