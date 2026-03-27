plugins {
    id("module")
}

dependencies {
    compileOnly(projects.detektApi)
    compileOnly(projects.detektPsiUtils)
    compileOnly(libs.jetbrains.annotations)

    testImplementation(libs.kotlin.compiler)
    testRuntimeOnly(projects.detektPsiUtils)
    testImplementation(projects.detektTest)
    testImplementation(projects.detektTestAssertj)
    testImplementation(libs.assertj.core)
    testImplementation(projects.detektApi)
}
