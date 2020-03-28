application {
    mainClassName = "io.gitlab.arturbosch.detekt.cli.Main"
}

val jcommanderVersion: String by project
val detektVersion: String by project
val kotlinxHtmlJvmVersion: String by project

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":detekt-core"))
    runtimeOnly(project(":detekt-rules"))
    implementation("com.beust:jcommander:$jcommanderVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:$kotlinxHtmlJvmVersion") {
        exclude(group = "org.jetbrains.kotlin")
    }

    testImplementation(project(":detekt-test"))
    testImplementation(project(":detekt-rules"))
}

// bundle detekt's version for debug logging on rule exceptions
tasks.withType<Jar> {
    manifest {
        attributes(mapOf("DetektVersion" to detektVersion))
    }
}
