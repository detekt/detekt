plugins {
    id("module")
}

dependencies {
    compileOnly(projects.detektApi)
    compileOnly(projects.detektMetrics)
    compileOnly(projects.detektTooling)
    testImplementation(projects.detektMetrics)
    testImplementation(projects.detektTest)
    testImplementation(libs.mockk)
    testImplementation(libs.assertj)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions.freeCompilerArgs.add("-Xcontext-receivers")
}
