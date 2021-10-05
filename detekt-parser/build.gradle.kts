import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion

plugins {
    id("module")
}

dependencies {
    api(libs.kotlin.compilerEmbeddable)
    implementation(projects.detektPsiUtils)
    testImplementation(projects.detektTestUtils)
    testImplementation(libs.bundles.testImplementation)
    testRuntimeOnly(libs.spek.runner)
}

tasks.withType<Test> {
    systemProperty("kotlinVersion", getKotlinPluginVersion() ?: embeddedKotlinVersion)

    doFirst {
        systemProperty("testClasspath", classpath.joinToString(";"))
    }
}
