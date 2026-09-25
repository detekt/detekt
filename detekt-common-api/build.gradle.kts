import dev.detekt.gradle.Detekt
import dev.detekt.gradle.DetektCreateBaselineTask

plugins {
    id("module")
    id("public-api")
    id("dev.drewhamilton.poko") version "0.23.1"
}

detekt {
    config.from("config/detekt.yml")
}

tasks {
    withType<Detekt>().configureEach {
        exclude("dev/detekt/detekt_api/BuildConfig.kt")
    }
    withType<DetektCreateBaselineTask>().configureEach {
        exclude("dev/detekt/detekt_api/BuildConfig.kt")
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
