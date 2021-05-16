plugins {
    java
    application
    id("org.openjfx.javafxplugin") version "0.0.10"
}

group = "org.glavo"
version = "4.0-beta1"

repositories {
    mavenCentral()
}

application {
    mainClass.set("org.glavo.viewer/org.glavo.viewer.Main")
}

javafx {
    version = "16"
    modules("javafx.controls")
}

tasks.compileJava {
    modularity.inferModulePath.set(true)
    options.javaModuleMainClass.set("org.glavo.viewer.Main")
    options.release.set(11)
    options.encoding = "UTF-8"
}

tasks.jar {
    manifest.attributes.putAll(
        mapOf(
            "Implementation-Version" to "1.2",
            "Main-Class" to "org.glavo.viewer.Main"
        )
    )
}
