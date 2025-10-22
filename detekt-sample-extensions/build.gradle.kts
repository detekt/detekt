plugins {
    kotlin("jvm") version "2.2.20"
}

dependencies {
    compileOnly("dev.detekt:detekt-api:2.0.0-alpha.0")
    testImplementation("dev.detekt:detekt-test:2.0.0-alpha.1")
    testImplementation("dev.detekt:detekt-test-assertj:2.0.0-alpha.1")
    testImplementation("org.assertj:assertj-core:3.27.6")
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
        }
    }
}
