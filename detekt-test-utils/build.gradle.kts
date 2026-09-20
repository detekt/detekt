plugins {
    id("module")
    id("public-api")
}

dependencies {
    implementation(projects.detektParser)
    implementation(libs.jetbrains.annotations)
    implementation(libs.kotlin.analysisApiImplementation)

    api(libs.kotlin.analysisApiIntellijApiSurfaceComponents)
    api(libs.kotlin.analysisApiSurface)
    implementation(libs.kotlin.analysisApiStandaloneSurface)
    runtimeOnly(libs.kotlin.analysisApiStandaloneImplementation)

    testImplementation(libs.assertj.core)
    testImplementation(projects.detektTestJunit)
}

kotlin {
    @OptIn(org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation::class)
    abiValidation {
        filters {
            exclude {
                byNames.add("dev.detekt.test.utils.internal.**")
            }
        }
    }
}
