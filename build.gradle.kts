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

tasks.processResources {
    dependsOn(convertSVG)
    from(convertSVG.map { it.outputDirectory })
}
