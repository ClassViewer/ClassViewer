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
package org.glavo.viewer.file.types.plain;

import org.glavo.viewer.file.VirtualFile;
import org.glavo.viewer.file.types.TextFileType;

import java.util.Set;

public final class PlainTextFileType extends TextFileType {
    public static final PlainTextFileType TYPE = new PlainTextFileType();

    private PlainTextFileType() {
        super("text", Set.of(
                "txt", "md", "asm",
                "c", "cc", "cpp", "cxx", "cs", "clj",
                "f", "for", "f90", "f95", "fs",
                "go", "gradle", "groovy", "g4",
                "h", "hpp", "hs",
                "java", "js", "jl",
                "kt", "kts",
                "m", "mm", "ml", "mli",
                "py", "pl",
                "ruby", "rs",
                "scala", "swift",
                "vala", "vapi",
                "zig",
                "sh", "bat", "ps1",
                "csv", "inf", "toml", "log"
        ));
    }

    @Override
    public boolean check(VirtualFile file, String ext) {
        if (super.check(file, ext)) {
            return true;
        }

        return switch (file.getFileName()) {
            case ".bashrc", ".zshrc", ".gitignore", "gradlew", "LICENSE" -> true;
            default -> false;
        };
    }
}
