package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.testkit.DslGradleRunner
import io.gitlab.arturbosch.detekt.testkit.ProjectLayout
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class DetektJvmSpec {

    @Nested
    inner class `report location set on extension & task` {
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
            """.trimIndent()
        ).also {
            it.setupProject()
        }

        @Test
        fun `logs a warning`() {
            gradleRunner.runTasksAndCheckResult(":detektMain") { buildResult ->
                assertThat(buildResult.output).contains(
                    "TXT report location set on detekt {} extension will be ignored for detektMain task."
                )
            }
        }
    }

    @Nested
    inner class `report location set on task only` {
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
            """.trimIndent()
        ).also {
            it.setupProject()
        }

        @Test
        fun `logs a warning`() {
            gradleRunner.runTasksAndCheckResult(":detektMain") { buildResult ->
                assertThat(buildResult.output).doesNotContain(
                    "report location set on detekt {} extension will be ignored"
                )
            }
        }
    }
}
