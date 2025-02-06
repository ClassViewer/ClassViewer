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

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.CountDownLatch;

/**
 * @author Glavo
 */
abstract class LogEvent {
    static final class DoLog extends LogEvent {
        final long time;
        final String caller;
        final Level level;
        final String message;
        final Throwable exception;

        DoLog(long time, String caller, Level level, String message, Throwable exception) {
            this.time = time;
            this.caller = caller;
            this.level = level;
            this.message = message;
            this.exception = exception;
        }
    }

    static final class ExportLog extends LogEvent {
        final CountDownLatch latch = new CountDownLatch(1);

        final OutputStream output;
        IOException exception;

        ExportLog(OutputStream output) {
            this.output = output;
        }

        void await() throws InterruptedException {
            latch.await();
        }
    }

    static final class Shutdown extends LogEvent {
    }
}
