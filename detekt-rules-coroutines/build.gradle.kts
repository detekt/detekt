plugins {
    id("module")
}

dependencies {
    compileOnly(projects.detektApi)
    compileOnly(projects.detektPsiUtils)

    testImplementation(libs.kotlin.compiler)
    testImplementation(projects.detektApi)
    testImplementation(projects.detektTest)
    testImplementation(projects.detektTestAssertj)
    testImplementation(projects.detektTestJunit)
    testImplementation(libs.assertj.core)
    testRuntimeOnly(libs.kotlinx.coroutinesCore)
    testRuntimeOnly(libs.kotlinx.coroutinesTest)
}
