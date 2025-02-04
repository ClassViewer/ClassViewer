package jlink;

import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;

public final class JdkPlatform implements Serializable {

    public static final JdkPlatform[] PLATFORMS = {
            new JdkPlatform(OS.WINDOWS, Arch.X86_64),
            new JdkPlatform(OS.WINDOWS, Arch.AARCH64),
            new JdkPlatform(OS.LINUX, Arch.X86_64),
            new JdkPlatform(OS.LINUX, Arch.AARCH64),
            new JdkPlatform(OS.LINUX, Arch.RISCV64),
            new JdkPlatform(OS.MACOS, Arch.X86_64),
            new JdkPlatform(OS.MACOS, Arch.AARCH64),
    };

    public final OS os;
    public final Arch arch;

    private JdkPlatform(OS os, Arch arch) {
        this.os = os;
        this.arch = arch;
    }

    @Override
    public String toString() {
        return os.name().toLowerCase(Locale.ROOT) + "-" + arch.name().toLowerCase(Locale.ROOT);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof JdkPlatform)) return false;
        JdkPlatform that = (JdkPlatform) o;
        return os == that.os && arch == that.arch;
    }

    @Override
    public int hashCode() {
        return Objects.hash(os, arch);
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
