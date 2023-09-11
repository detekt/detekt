import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    id("module")
    alias(libs.plugins.dokka)
    `java-test-fixtures`
    alias(libs.plugins.binaryCompatibilityValidator)
    alias(libs.plugins.poko)
}

dependencies {
    api(libs.kotlin.compilerEmbeddable)
    api(projects.detektPsiUtils)
    implementation(projects.detektUtils)

    testImplementation(projects.detektTest)
    testImplementation(libs.assertj)

    testFixturesApi(libs.kotlin.stdlibJdk8)
    testFixturesCompileOnly(libs.poko.annotations)
}

detekt {
    config.from("config/detekt.yml")
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
    outputDirectory = rootDir.resolve("website/static/kdoc")
}

tasks.dokkaHtml {
    notCompatibleWithConfigurationCache("https://github.com/Kotlin/dokka/issues/1217")
}

apiValidation {
    ignoredPackages.add("io.gitlab.arturbosch.detekt.api.internal")
}
