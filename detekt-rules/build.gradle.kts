configurations.testImplementation.extendsFrom(configurations["kotlinTest"])

tasks["build"].finalizedBy(":detekt-generator:generateDocumentation")

val junitPlatformVersion: String by project
val spekVersion: String by project
val reflectionsVersion: String by project

dependencies {
    implementation(project(":detekt-api"))
    implementation(kotlin("compiler-embeddable"))

    testImplementation("org.reflections:reflections:$reflectionsVersion")
    testImplementation(project(":detekt-test"))
    testImplementation(kotlin("reflect"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:$junitPlatformVersion")
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:$spekVersion")
}
