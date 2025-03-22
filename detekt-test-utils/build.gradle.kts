plugins {
    id("module")
    id("public-api")
}

dependencies {
    api(libs.kotlin.stdlib)
    api(libs.junit.jupiterApi)
    implementation(projects.detektParser)
    implementation(libs.kotlin.mainKts) {
        // Manually add the kotlin-scripting-compiler dependency instead to avoid future conflicts with Analysis API dependencies.
        isTransitive = false
    }
    implementation(libs.kotlin.scriptingCompiler)
    implementation(libs.kotlinx.coroutinesCore)

    testImplementation(libs.assertj.core)
}

apiValidation {
    ignoredPackages.add("io.github.detekt.test.utils.internal")
}
