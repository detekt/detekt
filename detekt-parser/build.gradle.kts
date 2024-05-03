import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion

plugins {
    id("module")
}

dependencies {
    api(libs.kotlin.compilerEmbeddable)
    testImplementation(projects.detektTestUtils)
    testImplementation(libs.assertj)
}

tasks.withType<Test>().configureEach {
    systemProperty("kotlinVersion", getKotlinPluginVersion())

    doFirst {
        systemProperty("testClasspath", classpath.joinToString(";"))
    }
}
