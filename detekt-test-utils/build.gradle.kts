import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("module")
    id("public-api")
}

dependencies {
    api(libs.kotlin.stdlib)
    api(libs.junit.jupiterApi)
    implementation(projects.detektKotlinAnalysisApi)
    implementation(projects.detektKotlinAnalysisApiStandalone)
    implementation(projects.detektParser)
    implementation(libs.kotlin.mainKts) {
        isTransitive = false
    }
    implementation(libs.kotlin.scriptingCompiler)
    implementation(libs.kotlinx.coroutinesCore)

    testImplementation(libs.assertj.core)
}

apiValidation {
    ignoredPackages.add("io.github.detekt.test.utils.internal")
}

java {
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile>().configureEach {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}
