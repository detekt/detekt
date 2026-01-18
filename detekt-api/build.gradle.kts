import dev.detekt.gradle.Detekt
import dev.detekt.gradle.DetektCreateBaselineTask

plugins {
    id("module")
    id("public-api")
    id("java-test-fixtures")
    id("dev.drewhamilton.poko") version "0.21.1"
    id("com.github.gmazzo.buildconfig") version "6.0.7"
}

dependencies {
    api(libs.kotlin.compiler)
    api(projects.detektKotlinAnalysisApi)

    testImplementation(projects.detektTest)
    testImplementation(projects.detektTestUtils)
    testImplementation(libs.assertj.core)

    testFixturesApi(libs.kotlin.compiler)
    testFixturesImplementation(projects.detektTestUtils)
}

detekt {
    config.from("config/detekt.yml")
}

buildConfig {
    buildConfigField("DETEKT_VERSION", Versions.DETEKT)
    buildConfigField("KOTLIN_IMPLEMENTATION_VERSION", libs.versions.kotlin.get())
}

tasks {
    withType<Detekt>().configureEach {
        exclude("dev/detekt/detekt_api/BuildConfig.kt")
    }
    withType<DetektCreateBaselineTask>().configureEach {
        exclude("dev/detekt/detekt_api/BuildConfig.kt")
    }
}

val javaComponent = components["java"] as AdhocComponentWithVariants
listOf(configurations.testFixturesApiElements, configurations.testFixturesRuntimeElements).forEach { config ->
    config.configure {
        javaComponent.withVariantsFromConfiguration(this) {
            skip()
        }
    }
}

kotlin {
    @OptIn(org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation::class)
    abiValidation {
        filters {
            exclude {
                byNames.add("dev.detekt.api.internal.**")
            }
        }
    }
}
