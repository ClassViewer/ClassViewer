package org.glavo.viewer.util;

import javafx.concurrent.Task;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class TaskUtils {
    private TaskUtils() {
    }

    public static final ExecutorService taskPool = Executors.newCachedThreadPool();

    public static final ExecutorService highlightPool = Executors.newSingleThreadExecutor();

    public static <T> Task<T> submit(Task<T> task) {
        taskPool.execute(task);
        return task;
    }

    public static <T> Task<T> submitHighlightTask(Callable<T> callable) {
        return submitHighlightTask(new Task<T>() {
            @Override
            protected T call() throws Exception {
                return callable.call();
            }
        });
    }

    public static <T> Task<T> submitHighlightTask(Task<T> task) {
        highlightPool.execute(task);
        return task;
    }


}
