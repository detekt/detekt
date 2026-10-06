plugins {
    kotlin("jvm") version "2.5.0-RC"
}

dependencies {
    compileOnly("dev.detekt:detekt-api:2.0.0-alpha.6")
    testImplementation("dev.detekt:detekt-test:2.0.0-alpha.6")
    testImplementation("dev.detekt:detekt-test-assertj:2.0.0-alpha.6")
    testImplementation("org.assertj:assertj-core:3.27.7")
}

testing {
    suites {
        named<JvmTestSuite>("test") {
            useJUnitJupiter()
        }
    }
}
