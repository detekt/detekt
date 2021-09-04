plugins {
    `kotlin-dsl`
}

repositories {
    mavenLocal() // used to publish and test local gradle plugin changes
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.kotlin.gradle)
    implementation(libs.detekt.gradle)
    implementation(libs.githubRelease.gradle)
    implementation(libs.semver4j.gradle)
    implementation(libs.nexusStaging.gradle)
}
