dependencies {
    implementation(project(":detekt-api"))
    implementation("com.pinterest.ktlint:ktlint-ruleset-standard:${Versions.KTLINT}") {
        exclude(group = "org.jetbrains.kotlin")
    }
    implementation("com.pinterest.ktlint:ktlint-core:${Versions.KTLINT}") {
        exclude(group = "org.jetbrains.kotlin")
    }
    implementation("com.pinterest.ktlint:ktlint-ruleset-experimental:${Versions.KTLINT}") {
        exclude(group = "org.jetbrains.kotlin")
    }

    testImplementation(project(":detekt-test"))
    testImplementation(project(":detekt-core"))
}

val depsToPackage = setOf(
    "org.ec4j.core",
    "com.pinterest.ktlint"
)

tasks.withType<Jar>().configureEach {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE // allow duplicates
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get()
            .filter { dependency -> depsToPackage.any { it in dependency.toString() } }
            .map { if (it.isDirectory) it else zipTree(it) }
    })
}
