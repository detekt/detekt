package dev.detekt.gradle.plugin

import io.gitlab.arturbosch.detekt.testkit.DslGradleRunner
import io.gitlab.arturbosch.detekt.testkit.ProjectLayout
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File

class ProblemsApiOutputReportFunctionalSpec {

    private lateinit var pluginJar: String

    @Test
    fun `incubating statement appearing from problems api`() {
        pluginJar = File(System.getProperty("user.dir"))
            .resolve("../detekt-report-problems-api/build/libs/detekt-report-problems-api-1.23.8.jar")
            .canonicalPath

        val gradleRunner = DslGradleRunner(
            projectLayout = ProjectLayout(
                numberOfSourceFilesInRootPerSourceDir = 0,
                srcDirs = listOf("src/main/kotlin")
            ),
            buildFileName = "build.gradle.kts",
            mainBuildFileContent = """
                plugins {
                    id("io.gitlab.arturbosch.detekt")
                }
                repositories {
                    mavenLocal()
                    mavenCentral()
                }
                dependencies {
                    detektPlugins(files("${pluginJar.replace('\\', '/')}"))
                    detektPlugins(gradleApi())
                }
                detekt {
                    allRules = true
                    ignoreFailures = false
                }
            """.trimIndent(),
            dryRun = false
        ).also {
            it.setupProject()

            val badClass = it.projectFile("src/main/kotlin/BadClass.kt")
            badClass.parentFile.mkdirs()
            badClass.writeText("class badClassName")
        }

        gradleRunner.runTasksAndExpectFailure("detekt") { result ->
            val output = result.output

            assertThat(output).contains("The file does not contain a package declaration.")
        }
    }
}
