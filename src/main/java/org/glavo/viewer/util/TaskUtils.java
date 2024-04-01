package org.glavo.viewer.util;

import javafx.concurrent.Task;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class TaskUtils {
    private TaskUtils() {
    }

    public static final ExecutorService taskPool = Executors.newCachedThreadPool();

    public static <T> Task<T> submit(Executor executor, Task<T> task) {
        executor.execute(task);
        return task;
    }

    public static <T> Task<T> submit(Executor executor, Callable<T> callable) {
        return submit(executor, new Task<>() {
            @Override
            protected T call() throws Exception {
                return callable.call();
            }
        });
    }

    public static <T> Task<T> submit(Task<T> task) {
        taskPool.execute(task);
        return task;
    }
}
