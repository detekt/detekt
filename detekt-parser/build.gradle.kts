import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion

dependencies {
    api(kotlin("compiler-embeddable"))
    implementation(project(":detekt-psi-utils"))
    testImplementation(project(":detekt-test-utils"))
}

tasks.withType<Test> {
    systemProperty("kotlinVersion", getKotlinPluginVersion() ?: embeddedKotlinVersion)

    doFirst {
        systemProperty("testClasspath", classpath.joinToString(";"))
    }
}
