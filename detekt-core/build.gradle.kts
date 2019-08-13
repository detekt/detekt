configurations.testImplementation.get().extendsFrom(configurations.kotlinTest.get())

val junitPlatformVersion: String by project
val spekVersion: String by project
val reflectionsVersion: String by project

dependencies {
    api(project(":detekt-api"))

    testImplementation(project(":detekt-rules"))
    testImplementation(project(":detekt-test"))
    testImplementation("org.reflections:reflections:$reflectionsVersion")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:$junitPlatformVersion")
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:$spekVersion")
}
