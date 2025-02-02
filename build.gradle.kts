plugins {
    java
    application
    id("org.glavo.compile-module-info-plugin") version "2.0"
}

group = "org.glavo"
version = "3.10"

val viewerModuleName = "org.glavo.viewer"
val viewerMainClassName = "org.glavo.viewer.Main"

repositories {
    mavenCentral()
}

application {
    mainClass.set(viewerMainClassName)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.compileJava {
    options.encoding = "UTF-8"
}

tasks.jar {
    manifest.attributes(
        "Main-Class" to viewerMainClassName
    )
}
