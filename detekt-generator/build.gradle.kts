import java.io.ByteArrayOutputStream

dependencies {
    implementation(project(":detekt-parser"))
    implementation(project(":detekt-api"))
    implementation(project(":detekt-rules"))
    implementation(project(":detekt-formatting"))
    implementation("com.beust:jcommander:${Versions.JCOMMANDER}")

    testImplementation(project(":detekt-test-utils"))
}

val documentationDir = "${rootProject.rootDir}/docs/pages/documentation"
val defaultConfigFile = "${rootProject.rootDir}/detekt-core/src/main/resources/default-detekt-config.yml"

val generateDocumentation by tasks.registering {
    dependsOn(tasks.build, ":detekt-api:dokka")
    description = "Generates detekt documentation and the default config.yml based on Rule KDoc"
    group = "documentation"

    inputs.files(
        fileTree("${rootProject.rootDir}/detekt-rules/src/main/kotlin"),
        fileTree("${rootProject.rootDir}/detekt-formatting/src/main/kotlin"),
        file("${rootProject.rootDir}/detekt-generator/build/libs/detekt-generator-${Versions.DETEKT}-all.jar"))
    outputs.files(
        fileTree(documentationDir),
        file(defaultConfigFile))

    doLast {
        javaexec {
            classpath(configurations.runtimeClasspath.get(), sourceSets.main.get().output)
            main = "io.gitlab.arturbosch.detekt.generator.Main"
            args = listOf(
                "--input",
                "${rootProject.rootDir}/detekt-rules/src/main/kotlin" + "," +
                    "${rootProject.rootDir}/detekt-formatting/src/main/kotlin",
                "--documentation",
                documentationDir,
                "--config",
                "${rootProject.rootDir}/detekt-cli/src/main/resources")
        }
    }
}

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
        commandLine = listOf("git", "diff", defaultConfigFile)
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
            "git", "diff", documentationDir, "${rootProject.rootDir}/docs/pages/kdoc"
        )
        standardOutput = configDiff
    }

    if (configDiff.toString().isNotEmpty()) {
        throw GradleException("The detekt documentation is not up-to-date. " +
            "Please build detekt locally to update it and commit the changed files.")
    }
}
