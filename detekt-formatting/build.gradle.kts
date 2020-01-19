configurations.testImplementation.get().extendsFrom(configurations.kotlinTest.get())

val ktlintVersion: String by project
val junitPlatformVersion: String by project
val spekVersion: String by project

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
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:$junitPlatformVersion")
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:$spekVersion")
}

tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE // allow duplicates
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get()
            .filter { "com.pinterest.ktlint" in it.toString() }
            .map { if (it.isDirectory) it else zipTree(it) }
    })
}
