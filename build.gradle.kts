import org.apache.batik.transcoder.TranscoderInput
import org.apache.batik.transcoder.TranscoderOutput
import org.apache.batik.transcoder.image.PNGTranscoder
import java.io.FileOutputStream
import java.io.OutputStream
import java.io.RandomAccessFile
import java.nio.file.Files


buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("org.glavo.kala:kala-platform:0.8.0")
        classpath("org.apache.xmlgraphics:batik-transcoder:1.14")
        classpath("org.apache.xmlgraphics:batik-codec:1.14")
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

val viewerMain = "org.glavo.viewer.Main"

repositories {
    maven(url = System.getenv("MAVEN_CENTRAL_MIRROR") ?: "https://repo1.maven.org/maven2/")
}

dependencies {
    implementation("org.glavo.kala:kala-platform:0.9.0")
    implementation("org.glavo.kala:kala-template:0.1.0")

    implementation("org.glavo:jimage:1.0.0")

    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.2.2")

    implementation("org.fxmisc.richtext:richtextfx:0.10.8")
}


apply {
    from("javafx.gradle.kts")
}

sourceSets {
    main {
        resources {
            exclude("**/*.svg")
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

val processSVG by tasks.creating {
    val resourcesPath = file("src/main/resources").toPath()
    val outputPath = buildDir.resolve("resources/images").toPath()

    val inputPaths = fileTree("src/main/resources") { include("**/*.svg") }.files.map { it.toPath() }
    val outputPaths = inputPaths.flatMap {
        val dir = outputPath.resolve(resourcesPath.relativize(it.parent))
        val fileNameBase = it.fileName.toString().substring(0, it.fileName.toString().length - 4)

        listOf(dir.resolve("$fileNameBase.png"), dir.resolve("$fileNameBase@2x.png"))
    }

    inputs.files(inputPaths)
    outputs.files(outputPaths)

    doLast {
        for (path in inputPaths) {
            val od = outputPath.resolve(resourcesPath.relativize(path.parent))
            Files.createDirectories(od)

            val fileNameBase = path.fileName.toString().substring(0, path.fileName.toString().length - 4)

            val transcoder = PNGTranscoder()

            val input = TranscoderInput(path.toUri().toString())
            Files.newOutputStream(od.resolve("$fileNameBase.png")).use { out ->
                transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, 16f)
                transcoder.transcode(input, TranscoderOutput(out))
            }

            Files.newOutputStream(od.resolve("$fileNameBase@2x.png")).use { out ->
                transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, 32f)
                transcoder.transcode(input, TranscoderOutput(out))
            }
        }
    }
}

tasks.processResources {
    dependsOn(":generateOpenJFXDependencies", ":processSVG")
    into("org/glavo/viewer") {
        from(project.buildDir.resolve("resources/openjfx/openjfx-dependencies.json"))
    }

    from(buildDir.resolve("resources/images"))
}

tasks.shadowJar {
    archiveClassifier.set(null as String?)
    manifest.attributes(
        "Implementation-Version" to "1.2",
        "Main-Class" to viewerMain,
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
