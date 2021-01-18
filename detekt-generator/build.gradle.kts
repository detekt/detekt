import java.io.ByteArrayOutputStream

plugins {
    module
}

dependencies {
    implementation(project(":detekt-parser"))
    implementation(project(":detekt-api"))
    implementation(project(":detekt-rules"))
    implementation(project(":detekt-rules-empty"))
    implementation(project(":detekt-formatting"))
    implementation("com.beust:jcommander")

    testImplementation(project(":detekt-test-utils"))
}

val documentationDir = "${rootProject.rootDir}/docs/pages/documentation"
val configDir = "${rootProject.rootDir}/detekt-core/src/main/resources"
val defaultConfigFile = "$configDir/default-detekt-config.yml"

val ruleModules = rootProject.subprojects
    .filter { "rules" in it.name }
    .map { it.name }
    .filterNot { it == "detekt-rules" }
    .map { "${rootProject.rootDir}/$it/src/main/kotlin" }

val generateDocumentation by tasks.registering {
    dependsOn(tasks.assemble, ":detekt-api:dokkaJekyll")
    description = "Generates detekt documentation and the default config.yml based on Rule KDoc"
    group = "documentation"

    inputs.files(
        ruleModules.map { fileTree(it) },
        fileTree("${rootProject.rootDir}/detekt-formatting/src/main/kotlin"),
        file("${rootProject.rootDir}/detekt-generator/build/libs/detekt-generator-${Versions.DETEKT}-all.jar")
    )

    outputs.files(
        fileTree(documentationDir),
        file(defaultConfigFile))

    doLast {
        javaexec {
            classpath(
                configurations.runtimeClasspath.get(),
                configurations.compileClasspath.get(),
                sourceSets.main.get().output
            )
            main = "io.gitlab.arturbosch.detekt.generator.Main"
            args = listOf(
                "--input",
                ruleModules.joinToString(",") + "," + "${rootProject.rootDir}/detekt-formatting/src/main/kotlin",
                "--documentation",
                documentationDir,
                "--config",
                configDir)
        }
    }
}

val verifyGeneratorOutput by tasks.registering {
    dependsOn(generateDocumentation)
    description = "Verifies that the default-detekt-config.yml is up-to-date"
    doLast {
        assertDefaultConfigUpToDate()
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
