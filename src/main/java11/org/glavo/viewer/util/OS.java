package org.glavo.viewer.util;

/**
 * The supported operating systems.
 */
public enum OS {
    /**
     * Unknown OS
     */
    UNKNOWN,

    /**
     * UNIX or Unix-like OS
     */
    UNIX,

    /**
     * IBM AIX
     */
    AIX(UNIX),

    /**
     * Oracle Solaris
     */
    SOLARIS(UNIX),

    /**
     * GNU/Linux OS
     */
    LINUX(UNIX),

    /**
     * Android
     */
    ANDROID(LINUX),

    /**
     * BSD-based OS
     */
    BSD(UNIX),

    /**
     * FreeBSD
     */
    FREEBSD(BSD),

    /**
     * OpenBSD
     */
    OPENBSD(BSD),

    /**
     * GNU/kFreeBSD
     */
    KFREEBSD(BSD),

    /**
     * NetBSD
     */
    NETBSD(BSD),

    /**
     * DragonFly BSD
     */
    DRAGONFLY(BSD),

    /**
     * Darwin OS
     */
    DARWIN(BSD),
    /**
     * Mac OS
     */
    MACOS(DARWIN),

    /**
     * IOS
     */
    IOS(DARWIN),

    /**
     * Windows
     */
    WINDOWS,

    /**
     * Windows Embedded Compact
     */
    WINDOWSCE(WINDOWS),
    ;

    private static final OS CURRENT = detectOS();

    private final String osName = this.name().toLowerCase();
    final OS base;

    OS() {
        this(null);
    }

    OS(OS base) {
        this.base = base;
    }

    public static OS getCurrent() {
        return CURRENT;
    }

    public String getOSName() {
        return osName;
    }

    public OS getBaseOS() {
        return base;
    }

    public boolean is(OS os) {
        if (os == null) {
            return false;
        }

        OS o = this;
        while (o != null) {
            if (o == os) {
                return true;
            }
            o = o.base;
        }
        return false;
    }

    public boolean isDarwin() {
        return this == DARWIN || this == MACOS || this == IOS;
    }

    public boolean isMac() {
        return this == MACOS;
    }

    public boolean isIOS() {
        return this == IOS;
    }

    public boolean isWindows() {
        return this == WINDOWS || this == WINDOWSCE;
    }

    public boolean isWindowsCE() {
        return this == WINDOWSCE;
    }

    public boolean isBSD() {
        return is(BSD);
    }

    public boolean isLinux() {
        return is(LINUX);
    }

    public boolean isUnix() {
        return is(UNIX);
    }

    @Override
    public String toString() {
        return getOSName();
    }

    private static OS detectOS() {
        String osName = System.getProperty("os.name", "").toLowerCase().trim();
        String jvmName = System.getProperty("java.vm.name", "").toLowerCase().trim();

        if (osName.startsWith("windows ce")) {
            return WINDOWSCE;
        }
        if (osName.startsWith("windows")) {
            return WINDOWS;
        }
        if (osName.startsWith("mac")) {
            return MACOS;
        }
        if (osName.startsWith("darwin")) {
            if ("robovm".equals(jvmName)) {
                return IOS;
            }
            return DARWIN;
        }

        if (osName.startsWith("linux") || osName.equals("gnu")) {
            if (jvmName.equals("dalvik")) {
                return ANDROID;
            }
            return LINUX;
        }

        if (osName.startsWith("aix")) {
            return AIX;
        }
        if (osName.startsWith("solaris") || osName.startsWith("sunos")) {
            return SOLARIS;
        }
        if (osName.startsWith("freebsd")) {
            return FREEBSD;
        }
        if (osName.startsWith("openbsd")) {
            return OPENBSD;
        }
        if (osName.startsWith("netbsd")) {
            return NETBSD;
        }
        if (osName.startsWith("dragonfly")) {
            return DRAGONFLY;
        }
        if (osName.equals("gnu/kfreebsd")) {
            return KFREEBSD;
        }

        return UNKNOWN;
    }
}
