plugins {
    id("module")
}

dependencies {
    compileOnly(projects.detektApi)
    compileOnly(projects.detektPsiUtils)

    testImplementation(libs.kotlin.compiler)
    testImplementation(projects.detektTest)
    testImplementation(libs.assertj.core)
    testRuntimeOnly(libs.kotlinx.coroutinesCore)
    testRuntimeOnly(libs.kotlinx.coroutinesTest)
}
