val assertjVersion: String by project
val junitPlatformVersion: String by project
val spekVersion: String by project

dependencies {
    // When creating a sample extension, change this dependency to the detekt-api version you build against
    // e.g. io.gitlab.arturbosch.detekt:detekt-api:1.x.x
    implementation(project(":detekt-api"))
    // When creating a sample extension, change this dependency to the detekt-test version you build against
    // e.g. io.gitlab.arturbosch.detekt:detekt-test:1.x.x
    testImplementation(project(":detekt-test"))
    // Do you want to write integration or system tests? Add the cli dependency.
    testImplementation(project(":detekt-cli"))

    testImplementation("org.assertj:assertj-core:$assertjVersion")
    testImplementation("org.spekframework.spek2:spek-dsl-jvm:$spekVersion")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher:$junitPlatformVersion")
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:$spekVersion")
}
