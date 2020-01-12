val ktlintVersion: String by project

dependencies {
    implementation(project(":detekt-api"))
    implementation("com.pinterest.ktlint:ktlint-ruleset-standard:$ktlintVersion") {
        exclude(group = "org.jetbrains.kotlin")
    }
    implementation("com.pinterest.ktlint:ktlint-core:$ktlintVersion") {
        exclude(group = "org.jetbrains.kotlin")
    }
    implementation("com.pinterest.ktlint:ktlint-ruleset-experimental:$ktlintVersion") {
        exclude(group = "org.jetbrains.kotlin")
    }
    testImplementation(project(":detekt-test"))
    testImplementation(project(":detekt-core"))
}

tasks.withType<Jar>().configureEach {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE // allow duplicates
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get()
            .filter { "com.pinterest.ktlint" in it.toString() }
            .map { if (it.isDirectory) it else zipTree(it) }
    })
}
