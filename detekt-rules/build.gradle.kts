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
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:$junitPlatformVersion")
    testRuntimeOnly("org.jetbrains.spek:spek-junit-platform-engine:$spekVersion")
}
