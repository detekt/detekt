package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.testkit.DslGradleRunner
import io.gitlab.arturbosch.detekt.testkit.ProjectLayout
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object DetektPlainSpec : Spek({
    describe("When detekt is applied before JVM plugin") {
        val gradleRunner = DslGradleRunner(
            projectLayout = ProjectLayout(numberOfSourceFilesInRootPerSourceDir = 1),
            buildFileName = "build.gradle",
            mainBuildFileContent = """
                plugins {
                    id "io.gitlab.arturbosch.detekt"
                    id "org.jetbrains.kotlin.jvm"
                }

                repositories {
                    mavenCentral()
                    mavenLocal()
                }

                detekt {
                }
            """.trimIndent(),
            dryRun = true
        )
        gradleRunner.setupProject()

        it("lazily adds detekt as a dependency of the `check` task") {
            gradleRunner.runTasksAndCheckResult("check") { buildResult ->
                assertThat(buildResult.task(":detekt")).isNotNull
            }
        }
    }

    describe("When applying detekt in a project") {
        val gradleRunner = DslGradleRunner(
            projectLayout = ProjectLayout(numberOfSourceFilesInRootPerSourceDir = 1),
            buildFileName = "build.gradle",
            baselineFiles = listOf("detekt-baseline.xml"),
            mainBuildFileContent = """
                plugins {
                    id "org.jetbrains.kotlin.jvm"
                    id "io.gitlab.arturbosch.detekt"
                }

                repositories {
                    mavenCentral()
                    mavenLocal()
                }

                detekt {
                    reports {
                        sarif.enabled = true
                        txt.enabled = false
                    }
                }
            """.trimIndent(),
            dryRun = true
        )
        gradleRunner.setupProject()

        it("configures detekt plain task") {
            gradleRunner.runTasksAndCheckResult(":detekt") { buildResult ->
                assertThat(buildResult.output).containsPattern("""--baseline \S*[/\\]detekt-baseline.xml """)
                assertThat(buildResult.output).contains("--report xml:")
                assertThat(buildResult.output).contains("--report sarif:")
                assertThat(buildResult.output).doesNotContain("--report txt:")
                assertThat(buildResult.output).doesNotContain("--classpath")
            }
        }
    }
})
