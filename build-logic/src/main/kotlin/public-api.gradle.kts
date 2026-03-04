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

tasks.check {
    // Add dependency manually until https://youtrack.jetbrains.com/issue/KT-80614 is fixed
    dependsOn("checkLegacyAbi")
}
