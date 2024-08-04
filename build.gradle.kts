/*
 * Copyright 2024 Glavo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
    id("com.github.johnrengelman.shadow") version "8.1.1"
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
    implementation("org.glavo.kala:kala-common:0.74.0")

    implementation("org.glavo:jimage:1.0.0")

    // https://mvnrepository.com/artifact/com.google.guava/guava
    implementation("com.google.guava:guava:33.1.0-jre")

    // https://mvnrepository.com/artifact/org.apache.commons/commons-compress
    implementation("org.apache.commons:commons-compress:1.26.2")

    // https://mvnrepository.com/artifact/com.google.code.gson/gson
    implementation("com.google.code.gson:gson:2.11.0")

    // https://mvnrepository.com/artifact/org.hildan.fxgson/fx-gson
    implementation("org.hildan.fxgson:fx-gson:5.0.0")

    // https://mvnrepository.com/artifact/org.fxmisc.richtext/richtextfx
    implementation("org.fxmisc.richtext:richtextfx:0.11.3")

    // https://mvnrepository.com/artifact/org.glavo/chardet
    implementation("org.glavo:chardet:2.4.0-beta1")

    // https://mvnrepository.com/artifact/org.apache.commons/commons-imaging
    implementation("org.apache.commons:commons-imaging:1.0.0-alpha5")

    // https://mvnrepository.com/artifact/org.apache.sshd/sshd-sftp
    implementation("org.apache.sshd:sshd-sftp:2.12.1")

    // https://mvnrepository.com/artifact/com.vladsch.flexmark/flexmark
    implementation("com.vladsch.flexmark:flexmark:0.64.8")

    implementation("org.tomlj:tomlj:1.1.1")

    implementation("org.controlsfx:controlsfx:11.2.1")

    val antlrVersion = "4.13.1"

    // https://mvnrepository.com/artifact/org.antlr/antlr4-runtime
    implementation("org.antlr:antlr4-runtime:$antlrVersion")
    antlr("org.antlr:antlr4:$antlrVersion")
}

configurations[JavaPlugin.API_CONFIGURATION_NAME].let { apiConfiguration ->
    apiConfiguration.setExtendsFrom(apiConfiguration.extendsFrom.filter { it.name != "antlr" })
}

sourceSets {
    main {
        resources {
            exclude("**/*.svg")
        }
    }
}

tasks.compileJava {
    options.release.set(22)
    options.encoding = "UTF-8"
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
    dependsOn(":processSVG")
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
