plugins {
    kotlin("jvm") version "2.2.10"
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("io.gitlab.arturbosch.detekt:detekt-api:1.23.8")
    testImplementation("io.gitlab.arturbosch.detekt:detekt-test:1.23.8")
    testImplementation("org.assertj:assertj-core:3.27.4")
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
        }
    }
}
