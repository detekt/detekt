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
    const val DETEKT = "1.7.4"
    const val GITHUB_RELEASE = "2.2.12"
}

dependencies {
    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:${Plugins.DETEKT}")
    implementation("com.github.breadmoirai:github-release:${Plugins.GITHUB_RELEASE}")
}
