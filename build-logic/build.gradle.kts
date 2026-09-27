import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.develocity.plugin)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.vanniktech.mavenPublish.plugin)
    implementation(libs.semver4j)
    implementation(libs.breadmoirai.githubRelease.plugin)
    implementation(libs.dokka.plugin)
}

kotlin {
    jvmToolchain(jdkVersion = 27)

    compilerOptions {
        jvmTarget = JvmTarget.entries.last()
        allWarningsAsErrors = providers.gradleProperty("warningsAsErrors").orNull.toBoolean()
    }
}

tasks.withType<JavaCompile>().configureEach {
    @Suppress("MagicNumber")
    options.release = JvmTarget.entries.last().target.toInt()
}
