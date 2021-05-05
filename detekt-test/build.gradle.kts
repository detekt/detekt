plugins {
    module
}

dependencies {
    api(project(":detekt-api"))
    api(project(":detekt-test-utils"))
    compileOnly(libs.assertj)
    implementation(project(":detekt-core"))
    implementation(project(":detekt-parser"))
}
