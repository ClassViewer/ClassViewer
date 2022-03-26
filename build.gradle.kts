import java.io.RandomAccessFile
import java.util.*

plugins {
    java
    application
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

group = "org.glavo"
version = "4.0-beta1".let {
    if (System.getProperty("viewer.release") == "true" || System.getenv("JITPACK") == "true") {
        it
    } else {
        "$it-SNAPSHOT"
    }
}

val viewerLauncher = "org.glavo.viewer.Launcher"
val viewerMain = "org.glavo.viewer.Main"

repositories {
    maven(url = System.getenv("MAVEN_CENTRAL_MIRROR") ?: "https://repo1.maven.org/maven2/")
    // maven(url = "https://jitpack.io")
    // mavenCentral()
}

dependencies {
    implementation("org.glavo:kala-platform:0.5.0")

    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-core
    implementation("com.fasterxml.jackson.core:jackson-core:2.13.2")

    annotationProcessor("com.github.bsideup.jabel:jabel-javac-plugin:0.4.2")
    annotationProcessor("net.java.dev.jna:jna-platform:5.10.0")
}

application {
    mainClass.set("org.glavo.viewer/$viewerMain")
}

tasks.compileJava {
    sourceCompatibility = "17"
    options.release.set(9)
    options.javaModuleMainClass.set(viewerMain)
    options.encoding = "UTF-8"

    options.compilerArgs.addAll(
        listOf(
            "-Xplugin:jabel"
        )
    )

    doLast {
        val tree = fileTree(destinationDirectory)
        tree.include("**/*.class")
        tree.exclude("module-info.class")
        tree.forEach {
            RandomAccessFile(it, "rw").use { rf ->
                rf.seek(7)   // major version
                rf.write(52)   // java 8
                rf.close()
            }
        }
    }
}

tasks.jar {
    enabled = false
    manifest.attributes(
        "Implementation-Version" to "1.2",
        "Main-Class" to viewerLauncher,
        "Add-Exports" to listOf(
            "java.base/jdk.internal.loader"
        ).joinToString(" ")
    )
}

tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {

}

val jfxModules = listOf("base", "graphics", "controls")
val jfxClassifier = listOf("linux", "linux-arm32-monocle", "linux-aarch64", "mac", "mac-aarch64", "win", "win-x86")
val jfxVersion = "17.0.1"
val jfxRepos = listOf("https://repo1.maven.org/maven2", "https://maven.aliyun.com/repository/central")

var jfxInClasspath = false

try {
    Class.forName("javafx.application.Application", false, project.javaClass.classLoader)
    jfxInClasspath = true
} catch (ignored: Throwable) {
}

if (!jfxInClasspath) {
    val osName = System.getProperty("os.name").toLowerCase(Locale.ROOT)
    val os = when {
        osName.startsWith("windows") -> "win"
        osName.startsWith("mac") -> "mac"
        osName.startsWith("linux") || osName == "gnu" -> "linux"
        else -> null
    }

    val arch = when (System.getProperty("os.arch").toLowerCase(Locale.ROOT)) {
        "x86_64", "x86-64", "amd64", "em64t" -> ""
        "x86", "x86-32", "x86_32", "i386", "i486", "i586", "i686", "i18pc" -> "-x86"
        "arm64", "aarch64" -> "-aarch64"
        "arm", "arm32", "aarch32" -> "-arm32-monocle"
        else -> null
    }

    val classifier = "$os$arch"

    if (os != null && arch != null && jfxClassifier.contains(classifier)) {
        dependencies {
            jfxModules.forEach { module ->
                compileOnly("org.openjfx:javafx-$module:$jfxVersion:$classifier")
            }
        }
    }
}