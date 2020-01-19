application {
    mainClassName = "io.gitlab.arturbosch.detekt.cli.Main"
}

val junitPlatformVersion: String by project
val spekVersion: String by project
val jcommanderVersion: String by project
val detektVersion: String by project
val reflectionsVersion: String by project
val mockkVersion: String by project

// implementation.extendsFrom kotlin is not enough for using cli in a gradle task - #58
configurations.testImplementation.get().extendsFrom(configurations.kotlinTest.get())

dependencies {
    implementation(project(":detekt-core"))
    runtimeOnly(project(":detekt-rules"))
    implementation("com.beust:jcommander:$jcommanderVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.6.12")

    testImplementation(project(":detekt-test"))
    testImplementation(project(":detekt-rules"))
    testImplementation("org.reflections:reflections:$reflectionsVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:$junitPlatformVersion")
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:$spekVersion")
}

// bundle detekt's version for debug logging on rule exceptions
tasks.withType<Jar> {
    manifest {
        attributes(mapOf("DetektVersion" to detektVersion))
    }
}
