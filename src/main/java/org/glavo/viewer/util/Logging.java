package org.glavo.viewer.util;

import org.glavo.viewer.CommandLineOptions;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public final class Logging {
    private Logging() {
    }

    public static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger("viewer");

    private static final SimpleDateFormat logTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private static final Formatter logFormatter = new Formatter() {
        @Override
        public String format(LogRecord record) {
            final StringBuilder builder = new StringBuilder();
            builder.append('[').append(logTimeFormat.format(new Date(record.getMillis()))).append("] [")
                    .append(record.getSourceClassName()).append('.').append(record.getSourceMethodName())
                    .append('/')
                    .append(record.getLevel()).append("] ")
                    .append(record.getMessage()).append('\n');

            if (record.getThrown() != null) {
                final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                try (final PrintWriter writer = new PrintWriter(buffer)) {
                    record.getThrown().printStackTrace(writer);
                }
                try {
                    builder.append(buffer.toString("UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    throw new AssertionError(e);
                }
            }

            return builder.toString();
        }
    };

    private static volatile boolean started = false;

    static {
        LOGGER.setLevel(Level.ALL);
        LOGGER.setUseParentHandlers(false);

        final ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.FINER);
        consoleHandler.setFormatter(logFormatter);
        LOGGER.addHandler(consoleHandler);
    }

    public static synchronized void start(Path home) {
        if (started) {
            throw new IllegalStateException();
        }
        try {
            Path logsDir = home.resolve("logs");
            Files.createDirectories(logsDir);

            Path logFile = logsDir.resolve(new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(new Date()) + ".log");

            final FileHandler fileHandle = new FileHandler(logFile.toAbsolutePath().toString());
            fileHandle.setLevel(Level.FINEST);
            fileHandle.setFormatter(logFormatter);
            fileHandle.setEncoding("UTF-8");

            LOGGER.addHandler(fileHandle);

            LOGGER.config("ClassViewer home: " + home);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Init Logger FileHandler failed", e);
        }

        started = true;
    }
}
