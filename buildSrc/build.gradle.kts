repositories {
    gradlePluginPortal()
}

dependencies {
    implementation(gradleApi())
    implementation("org.apache.commons:commons-compress:1.27.1")
    implementation("de.undercouch.download:de.undercouch.download.gradle.plugin:5.6.0")
    implementation("org.apache.xmlgraphics:batik-transcoder:1.18")
    implementation("org.apache.xmlgraphics:batik-codec:1.18")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}
