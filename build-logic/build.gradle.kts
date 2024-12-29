plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.kotlin.gradle)
    implementation(libs.gradleNexus.publish.plugin)
    implementation(libs.semver4j)
    implementation(libs.breadmoirai.githubRelease.plugin)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    val isCiBuild = System.getenv("CI") != null
    if (isCiBuild) {
        compilerExecutionStrategy.set(org.jetbrains.kotlin.gradle.tasks.KotlinCompilerExecutionStrategy.OUT_OF_PROCESS)
    }
}
