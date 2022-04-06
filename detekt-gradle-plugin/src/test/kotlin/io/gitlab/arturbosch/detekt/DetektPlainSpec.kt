package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.testkit.DslGradleRunner
import io.gitlab.arturbosch.detekt.testkit.ProjectLayout
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class DetektPlainSpec {
    @Nested
    inner class `When detekt is applied before JVM plugin` {
        val gradleRunner = DslGradleRunner(
            projectLayout = ProjectLayout(numberOfSourceFilesInRootPerSourceDir = 1),
            buildFileName = "build.gradle.kts",
            mainBuildFileContent = """
                plugins {
                    id("io.gitlab.arturbosch.detekt")
                    id("org.jetbrains.kotlin.jvm")
                }

                repositories {
                    mavenCentral()
                    mavenLocal()
                }

                detekt {
                }
            """.trimIndent(),
            dryRun = true
        ).also { it.setupProject() }

        @Test
        fun `lazily adds detekt as a dependency of the 'check' task`() {
            gradleRunner.runTasksAndCheckResult("check") { buildResult ->
                assertThat(buildResult.task(":detekt")).isNotNull
            }
        }
    }

    @Nested
    inner class `When applying detekt in a project` {
        val gradleRunner = DslGradleRunner(
            projectLayout = ProjectLayout(numberOfSourceFilesInRootPerSourceDir = 1),
            buildFileName = "build.gradle.kts",
            baselineFiles = listOf("detekt-baseline.xml"),
            mainBuildFileContent = """
                plugins {
                    id("org.jetbrains.kotlin.jvm")
                    id("io.gitlab.arturbosch.detekt")
                }

                repositories {
                    mavenCentral()
                    mavenLocal()
                }

                tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
                    reports {
                        sarif.enabled = true
                        txt.enabled = false
                    }
                }
            """.trimIndent(),
            dryRun = true
        ).also { it.setupProject() }

        @Test
        fun `configures detekt plain task`() {
            gradleRunner.runTasksAndCheckResult(":detekt") { buildResult ->
                assertThat(buildResult.output).containsPattern("""--baseline \S*[/\\]detekt-baseline.xml """)
                assertThat(buildResult.output).contains("--report xml:")
                assertThat(buildResult.output).contains("--report sarif:")
                assertThat(buildResult.output).doesNotContain("--report txt:")
                assertThat(buildResult.output).doesNotContain("--classpath")
            }
        }
    }
}
