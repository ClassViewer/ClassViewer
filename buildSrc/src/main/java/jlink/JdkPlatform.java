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
