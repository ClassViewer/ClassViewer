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
package jlink;

import java.util.Locale;

public enum JdkPlatform {
    WINDOWS_X86_64(OS.WINDOWS, Arch.X86_64),
    WINDOWS_AARCH64(OS.WINDOWS, Arch.AARCH64),
    LINUX_X86_64(OS.LINUX, Arch.X86_64),
    LINUX_AARCH64(OS.LINUX, Arch.AARCH64),
    LINUX_RISCV64(OS.LINUX, Arch.RISCV64),
    MACOS_X86_64(OS.MACOS, Arch.X86_64),
    MACOS_AARCH64(OS.MACOS, Arch.AARCH64),
    ;

    public final OS os;
    public final Arch arch;

    JdkPlatform(OS os, Arch arch) {
        this.os = os;
        this.arch = arch;
    }

    @Override
    public String toString() {
        return os.name().toLowerCase(Locale.ROOT) + "-" + arch.name().toLowerCase(Locale.ROOT);
    }

    public enum OS {
        WINDOWS,
        LINUX,
        MACOS;

        String getArchiveExtension() {
            return this == WINDOWS ? "zip" : "tar.gz";
        }
    }

    public enum Arch {
        X86_64,
        AARCH64,
        RISCV64
    }
}
