plugins {
    id("module")
    id("public-api")
}

dependencies {
    api(projects.detektCommonApi)
    testImplementation(libs.assertj.core)
}

kotlin {
    @OptIn(org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation::class)
    abiValidation {
        filters {
            exclude {
                byNames.add("dev.detekt.tooling.internal.**")
            }
        }
    }
}
