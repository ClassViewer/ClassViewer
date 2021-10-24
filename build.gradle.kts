import java.io.RandomAccessFile

plugins {
    java
    application
    id("org.openjfx.javafxplugin") version "0.0.10"
}

group = "org.glavo"
version = "4.0-beta1".let {
    if (System.getProperty("viewer.release") == "true" || System.getenv("JITPACK") == "true") {
        it
    } else {
        "$it-SNAPSHOT"
    }
}

val launcherClassName = "org.glavo.viewer.Launcher"
val mainClassName = "org.glavo.viewer.Main"

repositories {
    maven(url = System.getenv("MAVEN_CENTRAL_MIRROR") ?: "https://repo1.maven.org/maven2/")
}

dependencies {
    annotationProcessor("com.github.bsideup.jabel:jabel-javac-plugin:0.4.2")
    annotationProcessor("net.java.dev.jna:jna-platform:5.9.0")
}

javafx {
    version = "17.0.1"
    modules = listOf("javafx.controls")
}

application {
    mainClass.set("org.glavo.viewer/org.glavo.viewer.Launcher")
}

val java11 = sourceSets.create("java11") {
    java.srcDirs("src/main/java11")
}

tasks.compileJava {
    sourceCompatibility = "17"

    options.release.set(9)
    options.javaModuleMainClass.set(mainClassName)
    options.encoding = "UTF-8"

    options.compilerArgs.add("-Xplugin:jabel")

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

tasks.getByName<JavaCompile>("compileJava11Java") {
    sourceCompatibility = "11"
    targetCompatibility = "11"
    options.encoding = "UTF-8"
    options.compilerArgs.add("--add-exports=java.base/jdk.internal.loader=ALL-UNNAMED")
}

dependencies {
    "java11Implementation"(sourceSets.main.get().output.classesDirs)
}

tasks.jar {
    manifest.attributes(
        "Implementation-Version" to "1.2",
        "Main-Class" to launcherClassName,
        "Multi-Release" to "true",
        "Add-Exports" to listOf(
            "java.base/jdk.internal.loader"
        ).joinToString(" ")
    )

    into("META-INF/versions/11") {
        from(java11.output)
    }
}
