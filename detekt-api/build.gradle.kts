import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    module
    id("org.jetbrains.dokka")
    `java-test-fixtures`
    id("binary-compatibility-validator")
}

dependencies {
    api(libs.kotlin.compilerEmbeddable)
    api(project(":detekt-psi-utils"))

    testImplementation(project(":detekt-test"))

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

apiValidation {
    ignoredPackages.add("io.gitlab.arturbosch.detekt.api.internal")
}
