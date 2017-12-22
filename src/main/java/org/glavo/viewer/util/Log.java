package org.glavo.viewer.util;

import java.util.Objects;

public class Log {
    public static boolean color = !System.getProperty("os.name").toLowerCase().contains("win");

    static {
        String s = System.getProperty("viewer.color");
        if (s != null) {
            s = s.toLowerCase();
            if (s.equals("true"))
                color = true;
            else if (s.equals("false"))
                color = false;
        }
    }

    public static void log(Object message) {
        System.out.print("\u001b[32m\u001b[1m[INFO]\u001b[0m ");
        System.out.println(message);
    }

    public static void log(Throwable exception) {
        System.err.print("\u001b[31m\u001b[1m[ERROR]\u001b[0m ");
        exception.printStackTrace(System.err);
    }

    public static void info(String message) {
        System.out.print("\u001b[32m\u001b[1m[INFO]\u001b[0m ");
        System.out.println(message);
    }

    public static void warning(String message) {
        System.out.print("\u001b[33m\u001b[1m[WARNING]\u001b[0m ");
        System.out.println(message);
    }

    public static void error(String message) {
        System.err.print("\u001b[31m\u001b[1m[ERROR]\u001b[0m ");
        System.err.println(message);
    }

    public static void debug(Object obj) {
        System.out.print("\u001b[34m\u001b[1m[DEBUG]\u001b[0m ");
        System.out.println(Objects.toString(obj));
    }
}
