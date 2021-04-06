plugins {
    module
}

dependencies {
    compileOnly(project(":detekt-api"))
    implementation("io.github.detekt.sarif4j:sarif4j")
    testImplementation(project(":detekt-tooling"))
    testImplementation(project(":detekt-test-utils"))
    testImplementation(testFixtures(project(":detekt-api")))
}

tasks.withType<Jar>().configureEach {
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get()
            .asSequence()
            .filterNot { "org.jetbrains" in it.toString() }
            .filterNot { "org.intellij" in it.toString() }
            .map { if (it.isDirectory) it else zipTree(it) }
            .toList()
    })
}
