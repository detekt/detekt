package dev.detekt.gradle.plugin

import io.gitlab.arturbosch.detekt.testkit.DslGradleRunner
import io.gitlab.arturbosch.detekt.testkit.ProjectLayout
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.File

class ProblemsApiOutputReportFunctionalSpec {

    private lateinit var pluginJar: String
    private lateinit var runner: DslGradleRunner

    @BeforeEach
    fun setup() {
        pluginJar = File(System.getProperty("user.dir"))
            .resolve("../detekt-report-problems-api/build/libs/detekt-report-problems-api-1.23.8.jar")
            .canonicalPath

        runner = DslGradleRunner(
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
                    ignoreFailures = true
                }
                tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
                    reports {
                        custom {
                            reportId = "problemsAPI"
                            outputLocation.set(
                              file("build/reports/detekt/problemsAPI.txt")
                            )
                        }
                    }
                }
            """.trimIndent(),
            dryRun = false
        ).also {
            it.setupProject()
            val bad = it.projectFile("src/main/kotlin/BadClass.kt")
            bad.parentFile.mkdirs()
            // triggers 5 detekt issues
            bad.writeText("class badClassName")
        }
    }

    private fun makeRunner(ignoreFailures: Boolean, outputLocation: String) = DslGradleRunner(
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
                detektPlugins(files("$pluginJar"))
                detektPlugins(gradleApi())
            }
            detekt {
                allRules = true
                ignoreFailures = $ignoreFailures
            }
            tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
                reports {
                    custom {
                        reportId = "problemsAPI"
                        outputLocation.set(file("$outputLocation"))
                    }
                }
            }
        """.trimIndent(),
        dryRun = false
    ).also {
        it.setupProject()
        it.projectFile("src/main/kotlin/BadClass.kt").apply {
            parentFile.mkdirs()
            writeText("class badClassName")
        }
    }

    @Test
    @DisplayName("Fallback .txt report is generated with exact issue count for problems API")
    fun `problems api fallback txt report`() {
        runner.runTasksAndCheckResult("detekt") { _ ->
            val reportFile = runner.projectFile("build/reports/detekt/problemsAPI.txt")
            assertThat(reportFile).exists()

            val content = reportFile.readText().trim()
            assertThat(content)
                .isEqualTo("TEST-OK: Detekt found 5 issues.")
        }
    }

    @Test
    @DisplayName("Fallback HTML report is generated with exact issue count on failure")
    fun `fallback html report`() {
        val runner = makeRunner(
            ignoreFailures = false,
            outputLocation = "build/reports/problems/problems-report.html"
        )

        runner.runTasksAndExpectFailure("detekt") { /* no-op */ }

        // Check if the file exists
        val htmlFile = runner.projectFile("build/reports/problems/problems-report.html")
        assertThat(htmlFile).exists()

        // Check the content of the HTML file
        assertThat(htmlFile.readText().trim())
            .isEqualTo("TEST-OK: Detekt found 5 issues.")
    }

    @Test
    fun `incubating statement appearing from problems api`() {
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
                    // FAIL the build on errors so that Problems API is invoked
                    ignoreFailures = false
                }
                tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
                    reports {
                        custom {
                            reportId = "problemsAPI"
                        }
                    }
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

            assertThat(output).contains("[Incubating] Problems report is available at:")
        }
    }
}
