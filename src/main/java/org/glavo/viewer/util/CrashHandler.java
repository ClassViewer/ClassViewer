package org.glavo.viewer.util;

import org.glavo.viewer.util.logging.Log;

public final class CrashHandler implements Thread.UncaughtExceptionHandler {

    public static final CrashHandler INSTANCE = new CrashHandler();

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Log.error("Uncaught exception in thread " + t.getName());
        Log.shutdown();
    }
}
