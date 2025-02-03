package jlink;

import java.util.Locale;

public final class JdkPlatform {

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

    public JdkPlatform(OS os, Arch arch) {
        this.os = os;
        this.arch = arch;
    }

    public String getFileName(String version) {
        String osName = os.name().toLowerCase(Locale.ROOT);
        String archName = arch.name().toLowerCase(Locale.ROOT);
        String ext = os == OS.WINDOWS ? "zip" : "tar.gz";
        String suffix = arch == Arch.RISCV64 ? "" : "-full";

        if (arch == Arch.X86_64) {
            archName = "amd64";
        }

        return String.format(
                "bellsoft-jdk%4$s-%1$s-%2$s.%3$s",
                osName, archName, ext, version
        );
    }

    public String getDownloadUrl(String version) {
        return "https://download.bell-sw.com/java/" + version + "/" + getFileName(version);
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
