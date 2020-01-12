application {
    mainClassName = "io.gitlab.arturbosch.detekt.cli.Main"
}

val junitPlatformVersion: String by project
val spekVersion: String by project
val jcommanderVersion: String by project
val detektVersion: String by project
val reflectionsVersion: String by project
val mockkVersion: String by project

dependencies {
    implementation(project(":detekt-core"))
    runtimeOnly(project(":detekt-rules"))
    implementation("com.beust:jcommander:$jcommanderVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.6.12")

    testImplementation(project(":detekt-test"))
    testImplementation(project(":detekt-rules"))
}

// bundle detekt's version for debug logging on rule exceptions
tasks.withType<Jar>().configureEach {
    manifest {
        attributes(mapOf("DetektVersion" to detektVersion))
    }
}
