import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion

plugins {
    module
}

dependencies {
    api(libs.kotlin.compilerEmbeddable)
    implementation(projects.detektPsiUtils)
    testImplementation(projects.detektTestUtils)
}

tasks.withType<Test> {
    systemProperty("kotlinVersion", getKotlinPluginVersion() ?: embeddedKotlinVersion)

    doFirst {
        systemProperty("testClasspath", classpath.joinToString(";"))
    }
}
