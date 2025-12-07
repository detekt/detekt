plugins {
    id("module")
}

dependencies {
    api(libs.kotlin.compiler)
    testImplementation(libs.assertj.core)
}
