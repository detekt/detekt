plugins {
    `java-platform`
}

dependencies {
    val version = object {
        val spek = "2.0.13"
        val ktlint = "0.39.0"
    }

    constraints {
        api("org.assertj:assertj-core:3.17.2")
        api("org.spekframework.spek2:spek-dsl-jvm:${version.spek}")
        api("org.spekframework.spek2:spek-runner-junit5:${version.spek}")
        api("org.reflections:reflections:0.9.12")
        api("io.mockk:mockk:1.10.2")
        api("org.junit.platform:junit-platform-launcher:1.7.0")
        api("org.yaml:snakeyaml:1.27")
        api("com.beust:jcommander:1.78")
        api("com.pinterest.ktlint:ktlint-ruleset-standard:${version.ktlint}")
        api("com.pinterest.ktlint:ktlint-core:${version.ktlint}")
        api("com.pinterest.ktlint:ktlint-ruleset-experimental:${version.ktlint}")
        api("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.2")
        api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.8")
    }
}

publishing {
    publications.named<MavenPublication>(DETEKT_PUBLICATION) {
        from(components["javaPlatform"])
    }
}
