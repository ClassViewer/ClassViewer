import java.util.Properties

plugins {
    java
    application
    id("com.gradleup.shadow") version "8.3.6"
}

apply {
    plugin(jlink.JlinkPlugin::class)
}

group = "org.glavo"

version = System.getenv("GITHUB_SHA")?.lowercase()?.substring(0, 7).let { shortSha ->
    if (shortSha != null) {
        shortSha
    } else {
        val versionBase = property("viewer.version") as String
        if (findProperty("viewer.version.snapshot") == "false") {
            versionBase
        } else {
            "$versionBase-SNAPSHOT"
        }
    }
}

val viewerModuleName = "org.glavo.viewer"
val viewerMainClassName = "org.glavo.viewer.Main"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.jetbrains:annotations:26.0.2")
    implementation("org.glavo.kala:kala-base:0.80.0")
}

application {
    mainClass.set(viewerMainClassName)
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.compileJava {
    options.encoding = "UTF-8"
    options.javaModuleMainClass.set(viewerMainClassName)
}

tasks.withType<Jar> {
    manifest.attributes(
        "Main-Class" to viewerMainClassName
    )
}

val convertSVG by tasks.registering(svg.ConvertSVGTask::class) {
    inputDirectory.set(layout.projectDirectory.dir("src/main/svg"))
    outputDirectory.set(layout.buildDirectory.dir("generated/images"))
}

val downloadFont by tasks.registering(de.undercouch.gradle.tasks.download.Download::class) {
    src("https://github.com/JetBrains/JetBrainsMono/releases/download/v2.304/JetBrainsMono-2.304.zip")
    dest(layout.buildDirectory.dir("download"))
    overwrite(false)
}

val metadataFile = layout.buildDirectory.file("generated/metadata.properties")
val createMetadata by tasks.registering {
    val properties = mapOf(
        "viewer.version" to project.version.toString(),
    )

    inputs.properties(properties)
    outputs.file(metadataFile)

    doLast {
        val file = metadataFile.get().asFile
        file.parentFile.mkdirs()
        file.delete()

        file.writer().use { writer ->
            val p = Properties()
            properties.forEach { (k, v) -> p.setProperty(k, v) }
            p.store(writer, null)
        }
    }
}

tasks.processResources {
    dependsOn(convertSVG, downloadFont, createMetadata)

    from(convertSVG.map { it.outputDirectory })

    from(downloadFont.map { zipTree(it.outputFiles[0]) }) {
        include("**/JetBrainsMonoNL-Regular.ttf")
        includeEmptyDirs = false

        eachFile(object : Action<FileCopyDetails> {
            override fun execute(details: FileCopyDetails) {
                details.relativePath = RelativePath(
                    true,
                    *"org/glavo/viewer/resources/fonts/monospaced.ttf".split('/').toTypedArray()
                )
            }
        })
    }

    into("org/glavo/viewer/resources/") {
        from(metadataFile)
    }
}
