import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    id("org.jetbrains.kotlinx.binary-compatibility-validator")
    id("org.jetbrains.dokka")
}

tasks.withType<DokkaTask>().configureEach {
    failOnWarning = true
}
