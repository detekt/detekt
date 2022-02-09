import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    id("module")
    alias(libs.plugins.dokka)
    `java-test-fixtures`
    alias(libs.plugins.binaryCompatibilityValidator)
}

dependencies {
    api(libs.kotlin.compilerEmbeddable)
    api(projects.detektPsiUtils)
    implementation(projects.detektUtils)

    testImplementation(projects.detektTest)
    testImplementation(libs.assertj)

    testFixturesApi(libs.kotlin.stdlibJdk8)

    dokkaJekyllPlugin(libs.dokka.jekyll)
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
    outputDirectory.set(rootDir.resolve("docs/pages/kdoc"))
}

tasks.dokkaJekyll {
    notCompatibleWithConfigurationCache("https://github.com/Kotlin/dokka/issues/1217")
}

apiValidation {
    ignoredPackages.add("io.gitlab.arturbosch.detekt.api.internal")
}
