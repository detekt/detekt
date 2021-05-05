plugins {
    `kotlin-dsl`
}

repositories {
    mavenLocal() // used to publish and test local gradle plugin changes
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.detekt.gradlePlugin)
    implementation(libs.githubRelease.gradlePlugin)
    implementation(libs.shadow.gradlePlugin)
    implementation(libs.gradleVersions.gradlePlugin)
    implementation(libs.sonarqube.gradlePlugin)
    implementation(libs.dokka.gradlePlugin)
    implementation(libs.semver4j.gradlePlugin)
    implementation(libs.nexusStaging.gradlePlugin)
    implementation(libs.binaryCompatibilityValidator.gradlePlugin)
    implementation(libs.pluginPublishing.gradlePlugin)
}
