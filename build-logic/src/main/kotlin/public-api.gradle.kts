plugins {
    id("org.jetbrains.kotlinx.binary-compatibility-validator")
    id("org.jetbrains.dokka")
}

dokka {
    dokkaPublications.configureEach {
        failOnWarning = true
    }
}
