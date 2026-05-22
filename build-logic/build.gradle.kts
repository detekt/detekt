plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.develocity.plugin)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.gradleNexus.publish.plugin)
    implementation(libs.semver4j)
    implementation(libs.breadmoirai.githubRelease.plugin)
    implementation(libs.dokka.plugin)
}

kotlin {
    @Suppress("MagicNumber")
    jvmToolchain(17)

    compilerOptions {
        allWarningsAsErrors = providers.gradleProperty("warningsAsErrors").orNull.toBoolean()
    }
}
