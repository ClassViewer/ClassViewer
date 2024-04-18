/*
 * Copyright 2024 Glavo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
        final System.Logger.Level level;
        final String message;
        final Throwable exception;

        DoLog(long time, String caller, System.Logger.Level level, String message, Throwable exception) {
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
