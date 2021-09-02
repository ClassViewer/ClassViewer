import java.io.RandomAccessFile

plugins {
    java
    application
    id("org.openjfx.javafxplugin") version "0.0.10"
}

group = "org.glavo"
version = "4.0-beta1".let {
    if (System.getProperty("viewer.release") == "true") it else "$it-SNAPSHOT"
}

val launcherClassName = "org.glavo.viewer.Launcher"

repositories {
    mavenCentral()
}

application {
    mainClass.set("org.glavo.viewer/org.glavo.viewer.Launcher")
}

javafx {
    version = "16"
    modules("javafx.controls")
}

val java11 = sourceSets.register("java11") {
    java.srcDirs("src/main/java11")
}.get()

tasks.compileJava {
    options.release.set(9)
    options.encoding = "UTF-8"

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
    dependsOn(tasks.compileJava)
    classpath = sourceSets.main.get().output.classesDirs

    options.release.set(11)
    options.encoding = "UTF-8"
}

dependencies {
    "java11Implementation"(sourceSets.main.get().output.classesDirs)
}

tasks.jar {
    manifest.attributes(
        "Implementation-Version" to "1.2",
        "Main-Class" to launcherClassName,
        "Multi-Release" to "true"
    )

    into("META-INF/versions/11") {
        from(java11.output)
    }
}
