val assertjVersion: String by project
val junitPlatformVersion: String by project
val spekVersion: String by project

dependencies {
    // When creating a sample extension, change this dependency to the detekt-api version you build against, e.g.
    // io.gitlab.arturbosch.detekt:detekt-api:1.0.0-RC15
    implementation(project(":detekt-api"))
    implementation(kotlin("compiler-embeddable"))

    // When creating a sample extension, change this dependency to the detekt-test version you build against, e.g.
    // io.gitlab.arturbosch.detekt:detekt-test:1.0.0-RC15
    testImplementation(project(":detekt-test"))
    testImplementation("org.assertj:assertj-core:$assertjVersion")
    testImplementation("org.spekframework.spek2:spek-dsl-jvm:$spekVersion")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher:$junitPlatformVersion")
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:$spekVersion")
}
