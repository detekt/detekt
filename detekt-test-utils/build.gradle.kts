plugins {
    id("module")
    id("public-api")
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-ide-plugin-dependencies")
}

dependencies {
    api(libs.kotlin.stdlib)
    api(libs.junit.jupiterApi)
    runtimeOnly(projects.detektKotlinAnalysisApi)
    compileOnly(libs.bundles.kotlin.analysisApi) { isTransitive = false }
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
