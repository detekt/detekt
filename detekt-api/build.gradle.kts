import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    id("module")
    alias(libs.plugins.dokka)
    id("java-test-fixtures")
    alias(libs.plugins.binaryCompatibilityValidator)
    id("dev.drewhamilton.poko") version "0.16.0-beta01"
}

dependencies {
    api(libs.kotlin.compilerEmbeddable)
    api(projects.detektPsiUtils)
    implementation(projects.detektUtils)

    testImplementation(projects.detektTest)
    testImplementation(libs.assertj)
}

val javaComponent = components["java"] as AdhocComponentWithVariants
listOf(configurations.testFixturesApiElements, configurations.testFixturesRuntimeElements).forEach { config ->
    config.configure {
        javaComponent.withVariantsFromConfiguration(this) {
            skip()
        }
    }
}

tasks.withType<DokkaTask>().configureEach {
    outputDirectory.set(rootDir.resolve("website/static/kdoc"))
}

tasks.dokkaHtml {
    notCompatibleWithConfigurationCache("https://github.com/Kotlin/dokka/issues/1217")
}

apiValidation {
    ignoredPackages.add("io.gitlab.arturbosch.detekt.api.internal")
}
