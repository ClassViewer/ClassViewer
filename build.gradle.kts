plugins {
    java
    application
    id("org.openjfx.javafxplugin") version "0.0.10"
    id("org.beryx.jlink") version "2.24.1"
    id("com.gluonhq.gluonfx-gradle-plugin") version "1.0.4"
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
    mainClass.set("$viewerModuleName/$viewerMainClassName")
}

javafx {
    version = "21"
    modules("javafx.controls")
}

tasks.compileJava {
    options.release.set(8)
    options.javaModuleMainClass.set(viewerMainClassName)
    options.encoding = "UTF-8"
}

tasks.jar {
    manifest.attributes(
        "Implementation-Version" to "1.2",
        "Main-Class" to viewerMainClassName
    )
}

gluonfx {
    bundlesList = listOf("org.glavo.viewer.gui.ViewerResources")
    reflectionList = listOf("org.glavo.viewer.gui.Viewer", "javafx.scene.input.Dragboard")
    jniList = listOf("com.sun.glass.ui.CommonDialogs")
}

//jlink --strip-debug --no-header-files --no-man-pages --module-path ClassViewer-3.x.jar --add-modules org.glavo.viewer --output ClassViewer --strip-native-commands --vm=client
//jlink {
//    moduleName.set(viewerModuleName)
//    imageDir.set(file("$buildDir/ClassViewer"))
//    imageZip.set(file("$buildDir/ClassViewer-$version.zip"))
//
//    addOptions(
//        "--strip-debug",
//        "--no-header-files",
//        "--no-man-pages",
//        "--strip-native-commands"
//    )
//
//    project.tasks.getByName("jlink").doLast {
//        delete("$imageDir/bin/ClassViewer")
//        delete("$imageDir/bin/ClassViewer.bat")
//    }
//}