plugins {
    `kotlin-dsl`
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}

repositories {
    mavenLocal() // used to publish and test local gradle plugin changes
    gradlePluginPortal()
    jcenter()
}

object Plugins {
    const val KOTLIN = "1.4.21"
    const val DETEKT = "1.15.0"
    const val GITHUB_RELEASE = "2.2.12"
    const val SHADOW = "5.2.0"
    const val VERSIONS = "0.28.0"
    const val SONAR = "2.8"
    const val DOKKA = "1.4.10"
    const val SEMVER4J = "3.1.0"
    const val NEXUS = "0.22.0"
    const val KOTLIN_API_VALIDATOR = "0.3.0"
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${Plugins.KOTLIN}")
    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:${Plugins.DETEKT}")
    implementation("com.github.breadmoirai:github-release:${Plugins.GITHUB_RELEASE}")
    implementation("com.github.jengelman.gradle.plugins:shadow:${Plugins.SHADOW}")
    implementation("com.github.ben-manes:gradle-versions-plugin:${Plugins.VERSIONS}")
    implementation("org.sonarsource.scanner.gradle:sonarqube-gradle-plugin:${Plugins.SONAR}")
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:${Plugins.DOKKA}")
    implementation("com.vdurmont:semver4j:${Plugins.SEMVER4J}")
    implementation("io.codearte.gradle.nexus:gradle-nexus-staging-plugin:${Plugins.NEXUS}")
    implementation("org.jetbrains.kotlinx:binary-compatibility-validator:${Plugins.KOTLIN_API_VALIDATOR}")
}
