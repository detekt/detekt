plugins {
    id("module")
    id("public-api")
}

dependencies {
    compileOnly(projects.detektApi)
    compileOnly(libs.assertj.core)
}
