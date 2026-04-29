// This package can be retired once this is closed: https://youtrack.jetbrains.com/issue/KT-56203/AA-Publish-analysis-api-standalone-and-dependencies-to-Maven-Central

plugins {
    id("packaging")
    id("com.gradleup.shadow") version "9.4.1"
}

dependencies {
    // Exclude transitive dependencies due to https://youtrack.jetbrains.com/issue/KT-61639
    api(libs.kotlin.analysisApiStandalone) { isTransitive = false }
}

val defaultJarClassifier = "default-jar"

tasks.jar {
    archiveClassifier = defaultJarClassifier
}

configurations.runtimeElements {
    outgoing.artifacts.removeIf { it.classifier == defaultJarClassifier && it.extension == "jar" }
    outgoing.artifact(tasks.shadowJar)
}

configurations.apiElements {
    outgoing.variants.removeIf { it.name == "classes" }

    outgoing.artifacts.removeIf { it.classifier == defaultJarClassifier && it.extension == "jar" }
    outgoing.artifact(tasks.shadowJar)
}

tasks.shadowJar {
    archiveClassifier = ""
}

java {
    targetCompatibility = JavaVersion.VERSION_1_8
}

shadow {
    addShadowVariantIntoJavaComponent = false
}
