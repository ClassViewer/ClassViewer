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
package org.glavo.viewer.util.logging;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.CREATE_NEW;

public final class Log {
    static final String CLASS_NAME = Log.class.getName();

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.systemDefault());

    private static final BlockingQueue<LogEvent> queue = new LinkedBlockingQueue<>();
    private static final StringBuilder builder = new StringBuilder(512);

    private static Path logFile;
    private static ByteArrayOutputStream rawLogs;
    private static PrintWriter logWriter;

    private static Thread loggerThread;

    private static boolean shutdown = false;

    private static final int logRetention = 20;

    private static String format(LogEvent.DoLog event) {
        builder.setLength(0);
        builder.append('[');
        TIME_FORMATTER.formatTo(Instant.ofEpochMilli(event.time), builder);
        builder.append("] [")
                .append(event.caller)
                .append('/')
                .append(event.level)
                .append("] ")
                .append(event.message);
        return builder.toString();
    }

    private static void handle(LogEvent event) {
        if (event instanceof LogEvent.DoLog) {
            String log = format((LogEvent.DoLog) event);
            Throwable exception = ((LogEvent.DoLog) event).exception;

            System.out.println(log);
            if (exception != null)
                exception.printStackTrace(System.out);

            logWriter.println(log);
            if (exception != null)
                exception.printStackTrace(logWriter);
        } else if (event instanceof LogEvent.ExportLog) {
            LogEvent.ExportLog exportEvent = (LogEvent.ExportLog) event;
            logWriter.flush();
            try {
                if (logFile != null) {
                    Files.copy(logFile, exportEvent.output);
                } else {
                    rawLogs.writeTo(exportEvent.output);
                }
            } catch (IOException e) {
                exportEvent.exception = e;
            } finally {
                exportEvent.latch.countDown();
            }
        } else if (event instanceof LogEvent.Shutdown) {
            shutdown = true;
        } else {
            throw new AssertionError("Unknown event: " + event);
        }
    }

    private static void onExit() {
        shutdown();
        try {
            loggerThread.join();
        } catch (InterruptedException ignored) {
        }

        String caller = CLASS_NAME + ".onExit";

        if (logRetention > 0 && logFile != null) {
            List<Map.Entry<Path, int[]>> list = new ArrayList<>();
            Pattern fileNamePattern = Pattern.compile("(?<year>\\d{4})-(?<month>\\d{2})-(?<day>\\d{2})T(?<hour>\\d{2})-(?<minute>\\d{2})-(?<second>\\d{2})(\\.(?<n>\\d+))?\\.log(\\.(gz|xz))?");
            Path dir = logFile.getParent();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
                for (Path path : stream) {
                    Matcher matcher = fileNamePattern.matcher(path.getFileName().toString());
                    if (matcher.matches() && Files.isRegularFile(path)) {
                        int year = Integer.parseInt(matcher.group("year"));
                        int month = Integer.parseInt(matcher.group("month"));
                        int day = Integer.parseInt(matcher.group("day"));
                        int hour = Integer.parseInt(matcher.group("hour"));
                        int minute = Integer.parseInt(matcher.group("minute"));
                        int second = Integer.parseInt(matcher.group("second"));
                        int n = Optional.ofNullable(matcher.group("n")).map(Integer::parseInt).orElse(0);

                        list.add(Map.entry(path, new int[]{year, month, day, hour, minute, second, n}));
                    }
                }
            } catch (IOException e) {
                log(Level.WARNING, caller, "Failed to list log files in " + dir, e);
            }

            if (list.size() <= logRetention) {
                return;
            }

            list.sort((a, b) -> {
                int[] v1 = a.getValue();
                int[] v2 = b.getValue();

                assert v1.length == v2.length;

                for (int i = 0; i < v1.length; i++) {
                    int c = Integer.compare(v1[i], v2[i]);
                    if (c != 0)
                        return c;
                }

                return 0;
            });

            for (int i = 0, end = list.size() - logRetention; i < end; i++) {
                Path file = list.get(i).getKey();

                try {
                    if (!Files.isSameFile(file, logFile)) {
                        log(Level.INFO, caller, "Delete old log file " + file, null);
                        Files.delete(file);
                    }
                } catch (IOException e) {
                    log(Level.WARNING, caller, "Failed to delete log file " + file, e);
                }
            }
        }

        ArrayList<LogEvent> logs = new ArrayList<>();
        queue.drainTo(logs);
        for (LogEvent log : logs) {
            handle(log);
        }

        if (logFile == null) {
            return;
        }

        boolean failed = false;
        Path xzFile = logFile.resolveSibling(logFile.getFileName() + ".gz");
        try (GZIPOutputStream output = new GZIPOutputStream(Files.newOutputStream(xzFile))) {
            logWriter.flush();
            Files.copy(logFile, output);
        } catch (IOException e) {
            failed = true;
            handle(new LogEvent.DoLog(System.currentTimeMillis(), caller, Level.WARNING, "Failed to dump log file to xz format", e));
        } finally {
            logWriter.close();
        }

        if (!failed)
            try {
                Files.delete(logFile);
            } catch (IOException e) {
                System.err.println("An exception occurred while deleting raw log file");
                e.printStackTrace(System.err);
            }
    }

    public static void start(Path logFolder) {
        if (logFolder != null) {
            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss"));
            try {
                Files.createDirectories(logFolder);
                for (int n = 0; ; n++) {
                    Path file = logFolder.resolve(time + (n == 0 ? "" : "." + n) + ".log").toAbsolutePath().normalize();
                    try {
                        logWriter = new PrintWriter(Files.newBufferedWriter(file, UTF_8, CREATE_NEW));
                        logFile = file;
                        break;
                    } catch (FileAlreadyExistsException ignored) {
                    }
                }
            } catch (IOException e) {
                log(Level.WARNING, CLASS_NAME + ".start", "Failed to create log file", e);
            }
        }

        if (logWriter == null) {
            rawLogs = new ByteArrayOutputStream(256 * 1024);
            logWriter = new PrintWriter(new OutputStreamWriter(rawLogs, UTF_8));
        }

        loggerThread = new Thread(() -> {
            ArrayList<LogEvent> logs = new ArrayList<>();
            try {
                while (!shutdown) {
                    if (queue.drainTo(logs) > 0) {
                        for (LogEvent log : logs) {
                            handle(log);
                        }
                        logs.clear();
                    } else {
                        logWriter.flush();
                        handle(queue.take());
                    }
                }

                while (queue.drainTo(logs) > 0) {
                    for (LogEvent log : logs) {
                        handle(log);
                    }
                    logs.clear();
                }
            } catch (InterruptedException e) {
                throw new AssertionError("This thread cannot be interrupted", e);
            }
        });
        loggerThread.setName("Logger Thread");
        loggerThread.start();

        Thread cleanerThread = new Thread(Log::onExit);
        cleanerThread.setName("Logger Shutdown Hook");
        Runtime.getRuntime().addShutdownHook(cleanerThread);
    }

    public static void shutdown() {
        queue.add(new LogEvent.Shutdown());
    }

    public static Path getLogFile() {
        return logFile;
    }

    public static void exportLogs(OutputStream output) throws IOException {
        Objects.requireNonNull(output);
        LogEvent.ExportLog event = new LogEvent.ExportLog(output);
        try {
            queue.put(event);
            event.await();
        } catch (InterruptedException e) {
            throw new AssertionError("This thread cannot be interrupted", e);
        }
        if (event.exception != null) {
            throw event.exception;
        }
    }

    public static String getLogs() {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            exportLogs(output);
            return output.toString(UTF_8);
        } catch (IOException e) {
            log(Level.WARNING, CLASS_NAME + ".getLogs", "Failed to export logs", e);
            return "";
        }
    }

    private static void log(Level level, String caller, String msg, Throwable exception) {
        queue.add(new LogEvent.DoLog(System.currentTimeMillis(), caller, level, msg, exception));
    }

    public static void log(Level level, String msg) {
        log(level, CallerFinder.getCaller(), msg, null);
    }

    public static void log(Level level, String msg, Throwable exception) {
        log(level, CallerFinder.getCaller(), msg, exception);
    }

    public static void error(String msg) {
        log(Level.ERROR, CallerFinder.getCaller(), msg, null);
    }

    public static void error(String msg, Throwable exception) {
        log(Level.ERROR, CallerFinder.getCaller(), msg, exception);
    }

    public static void warning(String msg) {
        log(Level.WARNING, CallerFinder.getCaller(), msg, null);
    }

    public static void warning(String msg, Throwable exception) {
        log(Level.WARNING, CallerFinder.getCaller(), msg, exception);
    }

    public static void info(String msg) {
        log(Level.INFO, CallerFinder.getCaller(), msg, null);
    }

    public static void info(String msg, Throwable exception) {
        log(Level.INFO, CallerFinder.getCaller(), msg, exception);
    }

    public static void debug(String msg) {
        log(Level.DEBUG, CallerFinder.getCaller(), msg, null);
    }

    public static void debug(String msg, Throwable exception) {
        log(Level.DEBUG, CallerFinder.getCaller(), msg, exception);
    }

    public static void trace(String msg) {
        log(Level.TRACE, CallerFinder.getCaller(), msg, null);
    }

    public static void trace(String msg, Throwable exception) {
        log(Level.TRACE, CallerFinder.getCaller(), msg, exception);
    }
}
