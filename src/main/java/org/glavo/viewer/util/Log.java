package org.glavo.viewer.util;

import java.text.SimpleDateFormat;

import static org.glavo.viewer.gui.Options.color;
import static org.glavo.viewer.gui.Options.debug;

public final class Log {
    public static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public static void setting(String name, Object value) {
        if (color) {
            System.out.print("\u001b[35m\u001b[1m[Setting]\u001b[0m ");
            System.out.println("\u001b[34m\u001b[1m" + name + "\u001b[0m=\u001b[1m" + value + "\u001b[0m");
        } else {
            System.out.print("[Setting] ");
            System.out.println(name + "=" + value);
        }
    }

    public static void trace(Object obj) {
        if (color)
            System.out.print("\u001b[36m\u001b[1m[TRACE]\u001b[0m ");
        else
            System.out.print("[TRACE] ");
        System.out.println(obj);
    }

    public static void debug(Object obj) {
        if (!debug) return;
        if (color)
            System.out.print("\u001b[34m\u001b[1m[DEBUG]\u001b[0m ");
        else
            System.out.print("[DEBUG] ");
        System.out.println(obj);
    }

    public static void info(Object message) {
        if (color)
            System.out.print("\u001b[32m\u001b[1m[INFO]\u001b[0m ");
        else
            System.out.print("[INFO] ");
        System.out.println(message);
    }

    public static void warning(Object message) {
        if (color)
            System.out.print("\u001b[33m\u001b[1m[WARNING]\u001b[0m ");
        else
            System.out.print("[WARNING] ");
        System.out.println(message);
    }

    public static void error(Object message) {
        if (color)
            System.err.print("\u001b[31m\u001b[1m[ERROR]\u001b[0m ");
        else
            System.err.print("[ERROR] ");
        System.err.println(message);
    }


    public static void error(Throwable exception) {
        error(exception != null ? exception.getMessage() : "", exception);
    }


    public static void error(Object message, Throwable exception) {
        if (color)
            System.err.println("\u001b[31m\u001b[1m[ERROR]\u001b[0m " + message);
        else
            System.err.println("[ERROR] " + message);
        if (exception != null)
            exception.printStackTrace(System.err);
        else
            System.err.println("null");
    }

}

