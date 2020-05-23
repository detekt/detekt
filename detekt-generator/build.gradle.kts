import java.io.ByteArrayOutputStream

dependencies {
    implementation(project(":detekt-parser"))
    implementation(project(":detekt-api"))
    implementation(project(":detekt-rules"))
    implementation(project(":detekt-formatting"))
    implementation("com.beust:jcommander:${Versions.JCOMMANDER}")

    testImplementation(project(":detekt-test-utils"))
}

val shadowJar by tasks.registering(Jar::class) {
    archiveClassifier.set("all")
    from(configurations.runtimeClasspath.get().files.map { if (it.isDirectory) it else zipTree(it) })
    from(sourceSets.main.get().output)
    manifest {
        attributes.apply {
            put("Implementation-Title", project.name)
            put("Implementation-Version", Versions.DETEKT)
            put("Main-Class", "io.gitlab.arturbosch.detekt.generator.Main")
        }
    }
}

val generateDocumentation by tasks.registering {
    dependsOn(shadowJar, ":detekt-api:dokka")
    description = "Generates detekt documentation and the default config.yml based on Rule KDoc"
    group = "documentation"

    inputs.files(
        fileTree("${rootProject.rootDir}/detekt-rules/src/main/kotlin"),
        file("${rootProject.rootDir}/detekt-generator/build/libs/detekt-generator-${Versions.DETEKT}-all.jar"))
    outputs.files(
        fileTree("${rootProject.rootDir}/detekt-generator/documentation"),
        file("${rootProject.rootDir}/detekt-cli/src/main/resources/default-detekt-config.yml"))

    doLast {
        javaexec {
            main = "-jar"
            args = listOf(
                "${rootProject.rootDir}/detekt-generator/build/libs/detekt-generator-${Versions.DETEKT}-all.jar",
                "--input",
                "${rootProject.rootDir}/detekt-rules/src/main/kotlin" + "," +
                    "${rootProject.rootDir}/detekt-formatting/src/main/kotlin",
                "--documentation",
                "${rootProject.rootDir}/docs/pages/documentation",
                "--config",
                "${rootProject.rootDir}/detekt-cli/src/main/resources")
        }
    }
}

tasks.build.configure { dependsOn(shadowJar) }

val verifyGeneratorOutput by tasks.registering {
    dependsOn(generateDocumentation)
    description = "Verifies that all documentation and the config.yml are up-to-date"
    doLast {
        assertDefaultConfigUpToDate()
        assertDocumentationUpToDate()
    }
}

fun assertDefaultConfigUpToDate() {
    val configDiff = ByteArrayOutputStream()
    exec {
        commandLine = listOf("git", "diff",
            "${rootProject.rootDir}/detekt-cli/src/main/resources/default-detekt-config.yml")
        standardOutput = configDiff
    }

    if (configDiff.toString().isNotEmpty()) {
        throw GradleException("The default-detekt-config.yml is not up-to-date. " +
            "You can execute the generateDocumentation Gradle task to update it and commit the changed files.")
    }
}

fun assertDocumentationUpToDate() {
    val configDiff = ByteArrayOutputStream()
    exec {
        commandLine = listOf(
            "git", "diff", "${rootProject.rootDir}/docs/pages/documentation", "${rootProject.rootDir}/docs/pages/kdoc"
        )
        standardOutput = configDiff
    }

    if (configDiff.toString().isNotEmpty()) {
        throw GradleException("The detekt documentation is not up-to-date. " +
            "Please build detekt locally to update it and commit the changed files.")
    }
}
