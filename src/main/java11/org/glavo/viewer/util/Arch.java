package org.glavo.viewer.util;

/**
 * The supported CPU architectures.
 */
public enum Arch {

    /**
     * Unknown Arch
     */
    UNKNOWN(false),

    /**
     * X86
     */
    X86(false),

    /**
     * X86_64
     */
    X86_64(true),

    /**
     * ARM 32 bit
     */
    ARM(false),

    /**
     * ARM 64 bit (AARCH64)
     */
    ARM64(true),

    /**
     * Power PC 32 bit
     */
    PPC(false),

    /**
     * Power PC 64 bit
     */
    PPC64(true),

    /**
     * Power PC 64 bit little endian
     */
    PPC64LE(true),

    /**
     * IBM zSeries S/390 64 bit
     */
    S390X(true),

    /**
     * Sun sparc 32 bit
     */
    SPARC(false),

    /**
     * Sun sparc 64 bit
     */
    SPARCV9(true),

    /**
     * MIPS 32 bit
     */
    MIPS(false),

    /**
     * MIPS 64 bit
     */
    MIPS64(true),

    /**
     * MIPS 32 bit big endian
     */
    MIPSEL(false),

    /**
     * MIPS 64 bit big endian
     */
    MIPS64EL(true),
    ;
    private static final Arch CURRENT = detectArch();

    private final String archName = name().toLowerCase();
    private final boolean is64bit;

    Arch(boolean is64bit) {
        this.is64bit = is64bit;
    }

    public static Arch getCurrent() {
        return CURRENT;
    }

    public String getArchName() {
        return archName;
    }

    public boolean is64Bit() {
        return is64bit;
    }

    @Override
    public String toString() {
        return getArchName();
    }

    private static Arch detectArch() {
        String arch = System.getProperty("os.arch").toLowerCase().trim();
        switch (arch) {
            case "x86":
            case "i386":
            case "i486":
            case "i586":
            case "i686":
                return X86;
            case "x64":
            case "x86-64":
            case "x86_64":
            case "amd64":
                return X86_64;
            case "ppc":
            case "powerpc":
                return PPC;
            case "ppc64":
            case "powerpc64":
                if ("little".equals(System.getProperty("sun.cpu.endian"))) {
                    return PPC64LE;
                }
                return PPC64;
            case "ppc64le":
            case "powerpc64le":
                return PPC64LE;
            case "s390":
            case "s390x":
                return S390X;
            case "sparc":
                return SPARC;
            case "sparv9c":
                return SPARCV9;
            case "mips":
                return MIPS;
            case "mips64":
                return MIPS64;
            case "mipsel":
                return MIPSEL;
            case "mips64el":
                return MIPS64EL;
        }

        if (arch.startsWith("aarch64") || arch.startsWith("armv8") || arch.startsWith("arm64")) {
            return ARM64;
        } else if (arch.startsWith("arm")) {
            return ARM;
        }

        return UNKNOWN;
    }
}