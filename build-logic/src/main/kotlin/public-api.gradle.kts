import org.jetbrains.dokka.gradle.DokkaTaskPartial

plugins {
    id("org.jetbrains.kotlinx.binary-compatibility-validator")
    id("org.jetbrains.dokka")
}

tasks.withType<DokkaTaskPartial>().configureEach {
    notCompatibleWithConfigurationCache("https://github.com/Kotlin/dokka/issues/1217")
}
