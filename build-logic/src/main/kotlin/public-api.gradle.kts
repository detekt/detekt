plugins {
    id("org.jetbrains.dokka")
    kotlin("jvm")
}

dokka {
    dokkaPublications.configureEach {
        failOnWarning = true
    }
}

kotlin {
    @OptIn(org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation::class)
    abiValidation {
        enabled = true
    }
}
