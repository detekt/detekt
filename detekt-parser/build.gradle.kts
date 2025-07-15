import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion

plugins {
    id("module")
}

dependencies {
    api(libs.kotlin.compiler)
    implementation(projects.detektKotlinAnalysisApiStandalone)
    testImplementation(projects.detektTestUtils)
    testImplementation(libs.assertj.core)
}

tasks.withType<Test>().configureEach {
    systemProperty("kotlinVersion", getKotlinPluginVersion())

    doFirst {
        systemProperty("testClasspath", classpath.joinToString(";"))
    }
}
