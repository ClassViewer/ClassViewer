import java.io.RandomAccessFile

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
}

dependencies {
    implementation("org.glavo.kala:kala-platform:0.6.0")

    implementation("org.glavo:jimage:1.0.0")

    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.2")
}

application {
    mainClass.set(viewerMain)
}

apply {
    from("javafx.gradle.kts")
}

tasks.compileJava {
    sourceCompatibility = "9"
    targetCompatibility = "9"
    options.javaModuleMainClass.set(viewerMain)
    options.encoding = "UTF-8"

    modularity.inferModulePath.set(true)

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

tasks.processResources {
    dependsOn(":generateOpenJFXDependencies")
    into("org/glavo/viewer") {
        from(project.buildDir.resolve("openjfx").resolve("openjfx-dependencies.json"))
    }
}

tasks.shadowJar {
    minimize()
    archiveClassifier.set("")
}

tasks.jar {
    archiveClassifier.set("core")
    manifest.attributes(
        "Implementation-Version" to "1.2",
        "Main-Class" to viewerLauncher,
        "Add-Exports" to listOf(
            "java.base/jdk.internal.loader"
        ).joinToString(" ")
    )
}