repositories {
    gradlePluginPortal()
}

dependencies {
    implementation(gradleApi())
    implementation("org.apache.commons:commons-compress:1.27.1")
    implementation("de.undercouch.download:de.undercouch.download.gradle.plugin:5.6.0")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
