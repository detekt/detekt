configurations["implementation"].isCanBeResolved = true
configurations.testImplementation.get()
    .extendsFrom(configurations["kotlinTest"])

val ktlintVersion: String by project
val junitPlatformVersion: String by project
val spekVersion: String by project

dependencies {
    implementation(kotlin("compiler-embeddable"))
    implementation(project(":detekt-api"))
    implementation("com.github.shyiko.ktlint:ktlint-ruleset-standard:$ktlintVersion") {
        exclude(group = "org.jetbrains.kotlin")
    }
    implementation("com.github.shyiko.ktlint:ktlint-core:$ktlintVersion") {
        exclude(group = "org.jetbrains.kotlin")
    }

    testImplementation(project(":detekt-test"))
    testImplementation(project(":detekt-core"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:$junitPlatformVersion")
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:$spekVersion")
}

tasks.withType<Jar> {
    from(
        configurations["implementation"]
            .filter { "com.github.shyiko.ktlint" in it.toString() }
            .map { if (it.isDirectory) it else zipTree(it) }
    )
}
