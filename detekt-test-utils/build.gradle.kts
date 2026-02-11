import okio.ByteString

plugins {
    id("module")
    id("public-api")
    id("com.github.gmazzo.buildconfig") version "6.0.7"
}

dependencies {
    api(libs.kotlin.compiler)

    implementation(projects.detektKotlinAnalysisApi)
    implementation(projects.detektKotlinAnalysisApiStandalone)
    implementation(projects.detektParser)
    implementation(libs.kotlinx.coroutinesCore)
    implementation(libs.kotlinx.coroutinesTest)

    testImplementation(projects.detektTestJunit)
    testImplementation(libs.assertj.core)
}

kotlin {
    @OptIn(org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation::class)
    abiValidation {
        filters {
            excluded {
                byNames.add("dev.detekt.test.utils.internal.**")
            }
        }
    }
}

buildConfig {
    sourceSets.named("test") {
        buildConfigField("OKIO_JAR_PATH", ByteString::class.java.protectionDomain.codeSource.location.path)
    }
}
