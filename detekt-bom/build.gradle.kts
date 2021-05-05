plugins {
    `java-platform`
}

dependencies {
    constraints {
        api(libs.assertj)
        api(libs.spek.dsl)
        api(libs.spek.runner)
        api(libs.reflections)
        api(libs.mockk)
        api(libs.junitLauncher)
        api(libs.snakeyaml)
        api(libs.jcommander)
        api(libs.ktlint.rulesetStandard)
        api(libs.ktlint.core)
        api(libs.ktlint.rulesetExperimental)
        api(libs.kotlinx.html)
        api(libs.kotlinx.coroutines)
        api(libs.sarif4k)
    }
}

publishing {
    publications.named<MavenPublication>(DETEKT_PUBLICATION) {
        from(components["javaPlatform"])
    }
}
