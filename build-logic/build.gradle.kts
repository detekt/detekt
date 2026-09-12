plugins {
    `kotlin-dsl`
    id("com.gradleup.tapmoc") version "0.4.2"
}

dependencies {
    implementation(libs.develocity.plugin)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.vanniktech.mavenPublish.plugin)
    implementation(libs.semver4j)
    implementation(libs.breadmoirai.githubRelease.plugin)
    implementation(libs.dokka.plugin)
    implementation(libs.tapmoc.plugin)
}

kotlin {
    compilerOptions {
        allWarningsAsErrors = providers.gradleProperty("warningsAsErrors").orNull.toBoolean()
    }
}

tapmoc {
    gradle(gradle.gradleVersion)
}
