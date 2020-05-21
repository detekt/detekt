dependencies {
    api(kotlin("compiler-embeddable"))
    implementation(project(":detekt-psi-utils"))
    testImplementation(project(":detekt-test-utils"))
}

tasks.withType<Test> {
    systemProperty("kotlinVersion", embeddedKotlinVersion)

    doFirst {
        systemProperty("testClasspath", classpath.joinToString(";"))
    }
}
