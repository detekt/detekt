repositories {
    mavenLocal()
}

dependencies {
    compileOnly(project(":detekt-api"))
    compileOnly(project(":detekt-tooling"))
    implementation("io.github.detekt.sarif4j:sarif4j:1.0.0")
    testImplementation(project(":detekt-tooling"))
    testImplementation(testFixtures(project(":detekt-api")))
    testImplementation("io.rest-assured:json-path:4.3.1")
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
