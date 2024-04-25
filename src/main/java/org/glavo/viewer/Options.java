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
package org.glavo.viewer;

import kala.platform.Platform;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.glavo.viewer.util.logging.Logger.LOGGER;

public record Options(Path home, List<String> files) {
    private static Options options;

    public static void parse(String[] args) {
        if (options != null) {
            throw new IllegalStateException("command line arguments have been initialized");
        }

        String homeDir = null;
        List<String> files;

        int i = 0;
        loop:
        while (i < args.length) {
            String arg = args[i++];
            switch (arg) {
                case "--home":
                    homeDir = args[++i];
                    break;
                default:
                    break loop;
            }
        }

        if (i < args.length) {
            if (i == args.length - 1) {
                files = List.of(args[i]);
            } else {
                files = Arrays.asList(Arrays.copyOfRange(args, i, args.length));
            }
        } else {
            files = List.of();
        }

        Path home = null;
        if (homeDir != null) {
            try {
                home = Paths.get(homeDir);
            } catch (InvalidPathException e) {
                LOGGER.warning("home folder path is invalid", e);
                System.exit(1);
            }
        }

        if (home == null) {
            home = Platform.CURRENT_PLATFORM.getAppDataDirectory("Glavo").resolve("ClassViewer");
        }

        options = new Options(home, files);
    }

    public static Options getOptions() {
        assert options != null : "Options is not initialized";
        return options;
    }
}
