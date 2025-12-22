plugins {
    id("module")
    id("public-api")
}

dependencies {
    api(projects.detektApi)
    api(libs.kotlin.compiler)
    testImplementation(libs.assertj.core)
}

kotlin {
    @OptIn(org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation::class)
    abiValidation {
        filters {
            excluded {
                byNames.add("dev.detekt.tooling.internal.**")
            }
        }
    }
}
