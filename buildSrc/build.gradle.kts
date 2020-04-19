plugins {
    `kotlin-dsl`
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}

repositories {
    gradlePluginPortal()
    jcenter()
    mavenLocal() // used to publish and test local gradle plugin changes
}

object Plugins {
    const val KOTLIN = "1.3.72"
    const val DETEKT = "1.7.4"
    const val GITHUB_RELEASE = "2.2.12"
    const val ARTIFACTORY = "4.15.1"
    const val BINTRAY = "1.8.4"
    const val SHADOW = "5.2.0"
    const val VERSIONS = "0.28.0"
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${Plugins.KOTLIN}")
    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:${Plugins.DETEKT}")
    implementation("com.github.breadmoirai:github-release:${Plugins.GITHUB_RELEASE}")
    implementation("org.jfrog.buildinfo:build-info-extractor-gradle:${Plugins.ARTIFACTORY}")
    implementation("com.jfrog.bintray.gradle:gradle-bintray-plugin:${Plugins.BINTRAY}")
    implementation("com.github.jengelman.gradle.plugins:shadow:${Plugins.SHADOW}")
    implementation("com.github.ben-manes:gradle-versions-plugin:${Plugins.VERSIONS}")
}
