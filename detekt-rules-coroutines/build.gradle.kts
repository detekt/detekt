plugins {
    id("module")
}

dependencies {
    compileOnly(projects.detektApi)
    compileOnly(projects.detektPsiUtils)
    testImplementation(projects.detektTest)
    testImplementation(libs.assertj)
    testRuntimeOnly(libs.kotlinx.coroutines)
    testRuntimeOnly(libs.kotlinx.coroutines.test)
}
