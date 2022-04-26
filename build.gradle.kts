import java.io.RandomAccessFile

buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("org.glavo.kala:kala-platform:0.8.0")
    }
}


plugins {
    java
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
    implementation("org.glavo.kala:kala-platform:0.9.0")

    implementation("org.glavo:jimage:1.0.0")

    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.2.2")
}


apply {
    from("javafx.gradle.kts")
}

sourceSets {
    main {
        java {
            exclude("src/main/java/module-info.java")
        }
    }
}

tasks.compileJava {
    sourceCompatibility = "9"
    targetCompatibility = "9"
    options.javaModuleMainClass.set(viewerMain)
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

tasks.processResources {
    dependsOn(":generateOpenJFXDependencies")
    into("org/glavo/viewer") {
        from(project.buildDir.resolve("openjfx").resolve("openjfx-dependencies.json"))
    }
}

tasks.shadowJar {
    archiveClassifier.set(null as String?)
    manifest.attributes(
        "Implementation-Version" to "1.2",
        "Main-Class" to viewerLauncher,
        "Add-Exports" to listOf(
            "java.base/jdk.internal.loader"
        ).joinToString(" ")
    )
    minimize()

}

tasks.jar {
    archiveClassifier.set("core")
}

tasks.create<JavaExec>("run") {
    dependsOn(tasks.shadowJar)

    group = "application"

    classpath = files(tasks.shadowJar.get().archiveFile)
    workingDir = rootProject.rootDir
}
