plugins {
    id("module")
}

dependencies {
    testImplementation(kotlin("test"))
    compileOnly(gradleApi())
    implementation(projects.detektApi)
    testImplementation(projects.detektTest)
    testImplementation(gradleApi())
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
    testImplementation(gradleTestKit())
    testImplementation(libs.assertj.core)
}
