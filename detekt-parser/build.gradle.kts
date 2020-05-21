dependencies {
    api(kotlin("compiler-embeddable"))
}

tasks.withType<Test> {
    systemProperty("kotlinVersion", embeddedKotlinVersion)

    doFirst {
        systemProperty("testClasspath", classpath.joinToString(";"))
    }
}
