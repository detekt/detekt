plugins {
    `java-platform`
}

dependencies {
    val version = object {
        val spek = "2.0.11"
        val ktlint = "0.37.1"
    }

    constraints {
        api("org.assertj:assertj-core:3.16.1")
        api("org.spekframework.spek2:spek-dsl-jvm:${version.spek}")
        api("org.spekframework.spek2:spek-runner-junit5:${version.spek}")
        api("org.reflections:reflections:0.9.12")
        api("io.mockk:mockk:1.10.0")
        api("org.junit.platform:junit-platform-launcher:1.6.2")
        api("org.yaml:snakeyaml:1.26")
        api("com.beust:jcommander:1.78")
        api("com.pinterest.ktlint:ktlint-ruleset-standard:${version.ktlint}")
        api("com.pinterest.ktlint:ktlint-core:${version.ktlint}")
        api("com.pinterest.ktlint:ktlint-ruleset-experimental:${version.ktlint}")
        api("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.1")
        api("org.assertj:assertj-core:3.16.1")
    }
}
