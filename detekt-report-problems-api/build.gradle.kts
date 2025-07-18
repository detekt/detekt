plugins {
    id("module")
}

dependencies {
    testImplementation(kotlin("test"))
    compileOnly(gradleApi())
    compileOnly("io.gitlab.arturbosch.detekt:detekt-api:1.23.6")
    testImplementation("io.gitlab.arturbosch.detekt:detekt-test:1.23.6")
    testImplementation("io.gitlab.arturbosch.detekt:detekt-api:1.23.6")
    testImplementation(gradleApi())
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
    testImplementation(gradleTestKit())
}
