plugins {
    java
    application
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

application {
    mainClass.set(viewerMainClassName)
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.compileJava {
    options.encoding = "UTF-8"
    options.javaModuleMainClass.set(viewerMainClassName)
}

tasks.jar {
    manifest.attributes(
        "Main-Class" to viewerMainClassName
    )
}

val convertSVG by tasks.registering(svg.ConvertSVGTask::class) {
    inputDirectory.set(layout.projectDirectory.dir("src/main/svg"))
    outputDirectory.set(layout.buildDirectory.dir("generated/images"))
}

tasks.processResources {
    dependsOn(convertSVG)
    from(convertSVG.map { it.outputDirectory })
}
