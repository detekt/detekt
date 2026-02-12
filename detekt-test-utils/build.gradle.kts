import dev.detekt.gradle.Detekt
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

tasks.withType(Detekt::class).configureEach {
    exclude { it.path.contains("generated") }
}

buildConfig {
    sourceSets.named("test") {
        packageName("dev.detekt.generated")
        buildConfigField("OKIO_JAR_PATH", ByteString::class.java.protectionDomain.codeSource.location.path)
    }
}
