import org.apache.batik.transcoder.TranscoderInput
import org.apache.batik.transcoder.TranscoderOutput
import org.apache.batik.transcoder.image.PNGTranscoder
import java.nio.file.Files

buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("org.glavo.kala:kala-platform:0.10.0")
        classpath("org.apache.xmlgraphics:batik-transcoder:1.17")
        classpath("org.apache.xmlgraphics:batik-codec:1.17")
    }
}

plugins {
    java
    antlr
    id("com.github.johnrengelman.shadow") version "7.1.0"
    id("org.glavo.compile-module-info-plugin") version "2.0"
}

group = "org.glavo"
version = "4.0-beta1".let {
    if (System.getProperty("viewer.release") == "true" || System.getenv("JITPACK") == "true") {
        it
    } else {
        "$it-SNAPSHOT"
    }
}

val viewerMain = "org.glavo.viewer.Launcher"

repositories {
    maven(url = System.getenv("MAVEN_CENTRAL_MIRROR") ?: "https://repo1.maven.org/maven2/")
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains:annotations:24.0.1")

    implementation("org.glavo.kala:kala-platform:0.10.0")
    implementation("org.glavo.kala:kala-template:0.2.0")
    implementation("org.glavo.kala:kala-common:0.69.0")

    val kalaCompressVersion = "1.21.0.1-beta3"
    implementation("org.glavo.kala:kala-compress-archivers-zip:$kalaCompressVersion")
    implementation("org.glavo.kala:kala-compress-compressors-deflate64:$kalaCompressVersion")
    implementation("org.glavo.kala:kala-compress-compressors-bzip2:$kalaCompressVersion")

    implementation("org.glavo:jimage:1.0.0")

    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")

    implementation("org.fxmisc.richtext:richtextfx:0.11.2")
    implementation("com.github.albfernandez:juniversalchardet:2.4.0")
    implementation("org.apache.commons:commons-imaging:1.0-alpha3")
    implementation("com.jcraft:jsch:0.1.55")

    implementation("com.vladsch.flexmark:flexmark:0.64.0")

    val antlrVersion = "4.13.1"
    implementation("org.antlr:antlr4-runtime:$antlrVersion")

    antlr("org.antlr:antlr4:$antlrVersion")
}

configurations[JavaPlugin.API_CONFIGURATION_NAME].let { apiConfiguration ->
    apiConfiguration.setExtendsFrom(apiConfiguration.extendsFrom.filter { it.name != "antlr" })
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
    sourceCompatibility = "17"
    targetCompatibility = "17"
    options.encoding = "UTF-8"
}

tasks.named<org.glavo.mic.tasks.CompileModuleInfo>("compileModuleInfo") {
    moduleMainClass = viewerMain
}

val processSVG: Task by tasks.creating {
    val resourcesPath = file("src/main/resources").toPath()
    val outputPath = layout.buildDirectory.asFile.get().resolve("resources/images").toPath()

    val inputPaths = fileTree("src/main/resources") { include("**/*.svg") }.files.map { it.toPath() }
    val outputPaths = inputPaths.flatMap {
        val dir = outputPath.resolve(resourcesPath.relativize(it.parent))
        val fileNameBase = it.fileName.toString().substring(0, it.fileName.toString().length - 4)

        listOf(dir.resolve("$fileNameBase.png"), dir.resolve("$fileNameBase@2x.png"))
    }

    inputs.files(inputPaths)
    outputs.files(outputPaths)

    doLast {
        System.setProperty("java.awt.headless", "true")

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
        from(project.layout.buildDirectory.asFile.get().resolve("resources/openjfx/openjfx-dependencies.json"))
    }

    from(layout.buildDirectory.asFile.get().resolve("resources/images"))
}

val addOpens = listOf(
    "java.base/jdk.internal.loader",
    "javafx.graphics/javafx.scene.text",
    "javafx.graphics/com.sun.javafx.text",
    "javafx.graphics/com.sun.javafx.scene.text",
    "javafx.graphics/com.sun.javafx.geom",
)


tasks.shadowJar {
    archiveClassifier.set(null as String?)
    manifest.attributes(
        "Implementation-Version" to "1.2",
        "Main-Class" to viewerMain,
        "Add-Opens" to addOpens.joinToString(" ")
    )
    //minimize()
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

tasks.generateGrammarSource {

}