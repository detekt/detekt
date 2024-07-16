plugins {
    id("module")
    id("public-api")
    id("java-test-fixtures")
    id("dev.drewhamilton.poko") version "0.16.0"
}

dependencies {
    api(libs.kotlin.compilerEmbeddable)
    implementation(projects.detektUtils)

    testImplementation(projects.detektTest)
    testImplementation(libs.assertj)
    testFixturesImplementation(projects.detektTestUtils)
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

apiValidation {
    ignoredPackages.add("io.gitlab.arturbosch.detekt.api.internal")
}
