plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.kotlin.gradle)
    implementation(libs.githubRelease.gradle)
    implementation(libs.semver4j.gradle)
    implementation(libs.nexusStaging.gradle)
}

gradlePlugin {
    plugins {
        create("injectedPlugin") {
            id = "io.github.com.detekt.injected"
            implementationClass = "InjectedDependenciesPlugin"
        }
    }
}
