plugins {
    id("module")
}

dependencies {
    compileOnly(projects.detektApi)
    testImplementation(projects.detektTest)
    testImplementation(libs.assertj)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.freeCompilerArgs = listOf("-Xcontext-receivers")
}
