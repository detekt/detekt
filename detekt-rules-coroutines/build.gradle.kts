plugins {
    id("module")
}

dependencies {
    compileOnly(projects.detektApi)
    compileOnly(projects.detektPsiUtils)
    testImplementation(projects.detektTest)
    testImplementation(libs.assertj.core)
    testRuntimeOnly(libs.kotlinx.coroutinesCore)
    testRuntimeOnly(libs.kotlinx.coroutinesTest)
}
