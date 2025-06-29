plugins {
    id("module")
    id("public-api")
    id("java-test-fixtures")
    id("dev.drewhamilton.poko") version "0.19.0"
}

dependencies {
    api(libs.kotlin.compiler)
    api(projects.detektKotlinAnalysisApi)
    implementation(projects.detektUtils)

    testImplementation(projects.detektTest)
    testImplementation(libs.assertj.core)
    testFixturesImplementation(projects.detektTestUtils)
    testFixturesImplementation(libs.poko.annotations)
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
