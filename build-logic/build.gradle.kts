plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.plugins.kotlin.asDependency())
    implementation(libs.plugins.githubRelease.asDependency())
    implementation(libs.semver4j)
    implementation(libs.plugins.nexusPublish.asDependency())
    implementation(libs.plugins.binaryCompatibilityValidator.asDependency())
    implementation(libs.plugins.dokka.asDependency())
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

fun Provider<PluginDependency>.asDependency(): Provider<String> =
    this.map { "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}" }
