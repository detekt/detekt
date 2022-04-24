package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.testkit.DslGradleRunner
import io.gitlab.arturbosch.detekt.testkit.ProjectLayout
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * When applying detekt in a JVM project
 */
class DetektJvmSpec {

    @Test
    fun `type resolution on JVM`() {
        val fileContent = """
            package jvm.src.main.kotlin

            import kotlin.system.exitProcess
            
            class Errors {
                fun kotlinExit() {
                    exitProcess(0)
                }
            
                fun javaExit() {
                    @Suppress("ReplaceJavaStaticMethodWithKotlinAnalog")
                    System.exit(1)
                }
            }
        """.trimIndent()

        val detektConfigContent = """
            potential-bugs:
              ExitOutsideMain:
                active: true
        """.trimIndent()

        val gradleRunner = DslGradleRunner(
            projectLayout = ProjectLayout(
                numberOfSourceFilesInRootPerSourceDir = 0,
                numberOfCodeSmellsInRootPerSourceDir = 0,
                srcDirs = listOf("src/main/kotlin"),
            ),
            buildFileName = "build.gradle.kts",
            mainBuildFileContent = """
                plugins {
                    id("org.jetbrains.kotlin.jvm")
                    id("io.gitlab.arturbosch.detekt")
                }
                
                repositories {
                    mavenCentral()
                    mavenLocal()
                }
            """.trimMargin()
        )

        gradleRunner.setupProject()
        gradleRunner.writeProjectFile("src/main/kotlin/Errors.kt", fileContent)
        gradleRunner.writeProjectFile("config/detekt/detekt.yml", detektConfigContent)
        gradleRunner.runTasksAndExpectFailure("detektMain") { buildResult ->
            assertThat(buildResult.output).contains("failed with 2 weighted issues.")
            assertThat(buildResult.output).contains("Do not directly exit the process outside the `main` function. Throw an exception(…)")
            assertThat(buildResult.output).contains("Errors.kt:7:9")
            assertThat(buildResult.output).contains("Errors.kt:12:16")
        }
    }

    @Test
    fun `reporting location set on extension & task logs a warning`() {
        val gradleRunner = DslGradleRunner(
            projectLayout = ProjectLayout(numberOfSourceFilesInRootPerSourceDir = 1),
            buildFileName = "build.gradle.kts",
            mainBuildFileContent = """
                plugins {
                    kotlin("jvm")
                    id("io.gitlab.arturbosch.detekt")
                }

                repositories {
                    mavenCentral()
                    mavenLocal()
                }

                detekt {
                    reports {
                        txt.destination = file("output-path.txt")
                    }
                }

                tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
                    reports {
                        txt.destination = file("output-path2.txt")
                    }
                }
            """.trimIndent(),
            dryRun = false
        )
        gradleRunner.setupProject()
        gradleRunner.runTasksAndCheckResult(":detektMain") { buildResult ->
            assertThat(buildResult.output).contains("TXT report location set on detekt {} extension will be ignored for detektMain task.")
        }
    }

    @Test
    fun `reporting location set on task only logs a warning`() {
        val gradleRunner = DslGradleRunner(
            projectLayout = ProjectLayout(numberOfSourceFilesInRootPerSourceDir = 1),
            buildFileName = "build.gradle.kts",
            mainBuildFileContent = """
                plugins {
                    kotlin("jvm")
                    id("io.gitlab.arturbosch.detekt")
                }

                repositories {
                    mavenCentral()
                    mavenLocal()
                }

                tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
                    reports {
                        txt.destination = file("output-path2.txt")
                    }
                }
            """.trimIndent(),
            dryRun = false
        )
        gradleRunner.setupProject()
        gradleRunner.runTasksAndCheckResult(":detektMain") { buildResult ->
            assertThat(buildResult.output).doesNotContain("report location set on detekt {} extension will be ignored")
        }
    }
}
