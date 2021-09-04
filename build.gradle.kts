import java.io.RandomAccessFile

plugins {
    java
    application
    id("org.openjfx.javafxplugin") version "0.0.10"
    id("org.beryx.jlink") version "2.24.1"
}

group = "org.glavo"
version = "3.10"

val viewerModuleName = "org.glavo.viewer"
val viewerMainClassName = "org.glavo.viewer.Main"

repositories {
    mavenCentral()
}

application {
    mainClass.set("$viewerModuleName/$viewerMainClassName")
}

javafx {
    version = "16"
    modules("javafx.controls")
}

tasks.compileJava {
    options.release.set(9)
    options.javaModuleMainClass.set(viewerMainClassName)
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

tasks.jar {
    manifest.attributes(
        "Implementation-Version" to "1.2",
        "Main-Class" to viewerMainClassName
    )
}

//jlink --strip-debug --no-header-files --no-man-pages --module-path ClassViewer-3.x.jar --add-modules org.glavo.viewer --output ClassViewer --strip-native-commands --vm=client
jlink {
    moduleName.set(viewerModuleName)
    imageDir.set(file("$buildDir/ClassViewer"))
    imageZip.set(file("$buildDir/ClassViewer-$version.zip"))

    addOptions(
        "--strip-debug",
        "--no-header-files",
        "--no-man-pages",
        "--strip-native-commands"
    )

    project.tasks.getByName("jlink").doLast {
        delete("$imageDir/bin/ClassViewer")
        delete("$imageDir/bin/ClassViewer.bat")
    }
}