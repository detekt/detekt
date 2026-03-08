plugins {
    id("module")
    id("generator")
}

dependencies {
    compileOnly(projects.detektApi)

    testImplementation(libs.kotlin.compiler)
    testImplementation(projects.detektApi)
    testImplementation(projects.detektTest)
    testImplementation(projects.detektTestAssertj)
    testImplementation(libs.assertj.core)
}
