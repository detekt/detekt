import java.io.ByteArrayOutputStream

application {
    mainClassName = "io.gitlab.arturbosch.detekt.generator.Main"
}

val jar by tasks.getting(Jar::class) {
    manifest {
        attributes.apply { put("Main-Class", "io.gitlab.arturbosch.detekt.generator.Main") }
    }
}

// implementation.extendsFrom kotlin is not enough for using cli in a gradle task - #58
configurations.testImplementation.extendsFrom(configurations.kotlinTest)

val detektVersion: String by project

val generateDocumentation by tasks.creating {
    dependsOn(":detekt-generator:shadowJar")
    description = "Generates detekt documentation and the default config.yml based on Rule KDoc"

    inputs.files(
            fileTree("${rootProject.rootDir}/detekt-rules/src/main/kotlin"),
            file("${rootProject.rootDir}/detekt-generator/build/libs/detekt-generator-$detektVersion-all.jar"))
    outputs.files(
            fileTree("${rootProject.rootDir}/detekt-generator/documentation"),
            file("${rootProject.rootDir}/detekt-cli/src/main/resources/default-detekt-config.yml"))

    doLast {
        javaexec {
            main = "-jar"
            args = listOf(
                    "${rootProject.rootDir}/detekt-generator/build/libs/detekt-generator-$detektVersion-all.jar",
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

val verifyGeneratorOutput by tasks.creating {
    dependsOn(listOf(":detekt-generator:shadowJar", ":detekt-generator:generateDocumentation"))
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

    if (!configDiff.toString().isEmpty()) {
        throw GradleException("The default-detekt-config.yml is not up-to-date. " +
                "Please build detekt locally to update it and commit the changed files.")
    }
}

fun assertDocumentationUpToDate() {
    val configDiff = ByteArrayOutputStream()
    exec {
        commandLine = listOf("git", "diff", "${rootProject.rootDir}/docs/pages/documentation")
        standardOutput = configDiff
    }

    if (!configDiff.toString().isEmpty()) {
        throw GradleException("The detekt documentation is not up-to-date. " +
                "Please build detekt locally to update it and commit the changed files.")
    }
}

val junitPlatformVersion: String by project
val spekVersion: String by project
val jcommanderVersion: String by project

dependencies {
    implementation(project(":detekt-cli"))
    implementation(project(":detekt-core"))
    implementation(project(":detekt-rules"))
    implementation(project(":detekt-formatting"))
    implementation("com.beust:jcommander:$jcommanderVersion")
    implementation(kotlin("compiler-embeddable"))

    testImplementation(project(":detekt-test"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:$junitPlatformVersion")
    testRuntimeOnly("org.jetbrains.spek:spek-junit-platform-engine:$spekVersion")
}
