import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion

plugins {
    id("module")
}

dependencies {
    api(libs.kotlin.compilerEmbeddable)
    implementation(projects.detektPsiUtils)
    implementation(libs.contester.breakpoint)
    testImplementation(libs.contester.driver)
    testImplementation(projects.detektTestUtils)
    testImplementation(libs.assertj)
}

tasks.withType<Test> {
    systemProperty("kotlinVersion", getKotlinPluginVersion() ?: embeddedKotlinVersion)

    doFirst {
        systemProperty("testClasspath", classpath.joinToString(";"))
    }
}
