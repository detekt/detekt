val assertjVersion: String by project
val usedDetektVersion: String by project
val junitPlatformVersion: String by project
val spekVersion: String by project

dependencies {
    implementation("io.gitlab.arturbosch.detekt:detekt-api:$usedDetektVersion")
    implementation(kotlin("compiler-embeddable"))

    testImplementation("io.gitlab.arturbosch.detekt:detekt-test:$usedDetektVersion")
    testImplementation("org.assertj:assertj-core:$assertjVersion")
    testImplementation("org.spekframework.spek2:spek-dsl-jvm:$spekVersion")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher:$junitPlatformVersion")
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:$spekVersion")
}
