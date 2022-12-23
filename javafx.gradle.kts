buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("com.google.code.gson:gson:2.8.1")
        classpath("com.fasterxml.jackson.core:jackson-core:2.13.2")
    }
}

val jfxVersion = "19"

data class Platform(
    val name: String,
    val classifier: String,
    val groupId: String = "org.openjfx",
    val version: String = jfxVersion,
    val unsupportedModules: List<String> = listOf()
) {
    val modules: List<String> = jfxModules.filter { it !in unsupportedModules }

    fun fileUrl(
        module: String, classifier: String, ext: String,
        repo: String = System.getenv("MAVEN_CENTRAL_MIRROR") ?: "https://repo1.maven.org/maven2"
    ): java.net.URL =
        java.net.URL(
            "$repo/${groupId.replace('.', '/')}/javafx-$module/$version/javafx-$module-$version-$classifier.$ext"
        )
}

val jfxModules = listOf("base", "graphics", "controls")
val jfxMirrorRepos = listOf("https://maven.aliyun.com/repository/central")
val jfxDependenciesFile = rootProject.buildDir.resolve("resources/openjfx/openjfx-dependencies.json")
val jfxPlatforms = listOf(
    Platform("windows-x86", "win-x86"),
    Platform("windows-x86_64", "win"),
    Platform("macos-x86_64", "mac"),
    Platform("macos-aarch64", "mac-aarch64"),
    Platform("linux-x86_64", "linux"),
    Platform("linux-arm", "linux-arm32-monocle"),
    Platform("linux-aarch64", "linux-aarch64"),
)

val jfxInClasspath =
    try {
        Class.forName("javafx.application.Application", false, this.javaClass.classLoader)
        true
    } catch (ignored: Throwable) {
        false
    }

if (!jfxInClasspath && JavaVersion.current() >= JavaVersion.VERSION_11) {
    val os = System.getProperty("os.name").toLowerCase().let { osName ->
        when {
            osName.contains("win") -> "windows"
            osName.contains("mac") -> "osx"
            osName.contains("linux") || osName.contains("unix") -> "linux"
            else -> null
        }
    }

    val arch = when (System.getProperty("os.arch").toLowerCase()) {
        "x86_64", "x86-64", "amd64", "ia32e", "em64t", "x64" -> "x86_64"
        "x86", "x86_32", "x86-32", "i386", "i486", "i586", "i686", "i86pc", "ia32", "x32" -> "x86"
        "arm64", "aarch64", "armv8", "armv9" -> "arm64"
        else -> null
    }

    if (os != null && arch != null) {
        val platform = jfxPlatforms.find { it.name == "$os-$arch" }
        if (platform != null) {
            val groupId = platform.groupId
            val version = platform.version
            val classifier = platform.classifier
            rootProject.subprojects {
                for (module in jfxModules) {
                    dependencies.add("compileOnly", "$groupId:javafx-$module:$version:$classifier")
                }
            }
        }
    }
}

rootProject.tasks.create("generateOpenJFXDependencies") {
    outputs.file(jfxDependenciesFile)

    doLast {
        val jfxDependencies = jfxPlatforms.associate { platform ->
            platform.name to platform.modules.map { module ->
                mapOf(
                    "module" to "javafx.$module",
                    "groupId" to platform.groupId,
                    "artifactId" to "javafx-$module",
                    "version" to platform.version,
                    "classifier" to platform.classifier,
                    "sha1" to platform.fileUrl(module, platform.classifier, "jar.sha1").readText()
                )
            }
        }

        jfxDependenciesFile.parentFile.mkdirs()
        jfxDependenciesFile.writeText(
            com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(jfxDependencies)
        )
    }
}

// Ensure that the mirror repository caches files
rootProject.tasks.create("preTouchOpenJFXDependencies") {
    doLast {
        for (repo in jfxMirrorRepos) {
            for (platform in jfxPlatforms) {
                for (module in platform.modules) {
                    val url = platform.fileUrl(module, platform.classifier, "jar", repo = repo)
                    logger.quiet("Getting $url")
                    try {
                        url.readBytes()
                    } catch (e: Throwable) {
                        logger.warn("An exception occurred while pre touching $url", e)
                    }
                }
            }
        }
    }
}
