package org.glavo.viewer.util;

import javafx.concurrent.Task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class TaskUtils {
    private TaskUtils() {
    }

    public static final ExecutorService taskPool = Executors.newCachedThreadPool();

    public static <T> Task<T> submit(Task<T> task) {
        taskPool.execute(task);
        return task;
    }
}
