plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.develocity.plugin)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.gradleNexus.publish.plugin)
    implementation(libs.semver4j)
    implementation(libs.breadmoirai.githubRelease.plugin)
    implementation(libs.binaryCompatibilityValidator.plugin)
    implementation(libs.dokka.plugin)
}

kotlin {
    @Suppress("MagicNumber")
    jvmToolchain(8)

    compilerOptions {
        allWarningsAsErrors = providers.gradleProperty("warningsAsErrors").orNull.toBoolean()
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    val isCiBuild = providers.environmentVariable("CI").isPresent
    if (isCiBuild) {
        compilerExecutionStrategy = org.jetbrains.kotlin.gradle.tasks.KotlinCompilerExecutionStrategy.OUT_OF_PROCESS
    }
}
