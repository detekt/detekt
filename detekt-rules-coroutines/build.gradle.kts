plugins {
    id("module")
}

dependencies {
    compileOnly(projects.detektApi)
    testImplementation(projects.detektTest)
    testImplementation(libs.assertj)
    testRuntimeOnly(libs.kotlinx.coroutines)
    testRuntimeOnly(libs.kotlinx.coroutines.test)
}
