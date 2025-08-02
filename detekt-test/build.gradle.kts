import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("module")
    id("public-api")
}

dependencies {
    api(projects.detektApi)
    api(projects.detektTestUtils)
    implementation(projects.detektKotlinAnalysisApiStandalone)
    implementation(projects.detektUtils)
    implementation(libs.kotlin.reflect)
    compileOnly(libs.assertj.core)
    implementation(projects.detektCore)
}

java {
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile>().configureEach {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}
