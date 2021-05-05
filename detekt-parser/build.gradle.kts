import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion

plugins {
    module
}

dependencies {
    api(libs.kotlin.compilerEmbeddable)
    implementation(project(":detekt-psi-utils"))
    testImplementation(project(":detekt-test-utils"))
}

tasks.withType<Test> {
    systemProperty("kotlinVersion", getKotlinPluginVersion() ?: embeddedKotlinVersion)

    doFirst {
        systemProperty("testClasspath", classpath.joinToString(";"))
    }
}
