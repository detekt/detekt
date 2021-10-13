plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.kotlin.gradle)
    implementation(libs.githubRelease.gradle)
    implementation(libs.semver4j.gradle)
    implementation(libs.nexusStaging.gradle)
}
