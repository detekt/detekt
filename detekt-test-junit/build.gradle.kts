import dev.detekt.gradle.Detekt
import okio.ByteString

plugins {
    id("module")
    id("public-api")
    id("com.github.gmazzo.buildconfig") version "6.0.7"
}

dependencies {
    api(libs.junit.jupiterApi)
    implementation(projects.detektTestUtils)
    implementation(libs.kotlin.scriptingJvm)
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
