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
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    val isCiBuild = System.getenv("CI") != null
    if (isCiBuild) {
        compilerExecutionStrategy = org.jetbrains.kotlin.gradle.tasks.KotlinCompilerExecutionStrategy.OUT_OF_PROCESS
    }
    compilerOptions {
        allWarningsAsErrors = providers.gradleProperty("warningsAsErrors").orNull.toBoolean()
    }
}

fun Provider<PluginDependency>.asDependency(): Provider<String> =
    this.map { "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}" }
