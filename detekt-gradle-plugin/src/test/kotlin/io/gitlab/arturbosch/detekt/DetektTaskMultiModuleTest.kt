package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.DslTestBuilder.Companion.groovy
import io.gitlab.arturbosch.detekt.DslTestBuilder.Companion.kotlin
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

/**
 * @author Markus Schwarz
 */
internal class DetektTaskMultiModuleTest : Spek({
    describe("The Detekt Gradle plugin used in a multi module project") {
        describe(
            "is applied with defaults to all subprojects individually without sources in root project " +
                "using the subprojects block"
        ) {
            val projectLayout = ProjectLayout(0)
                .withSubmodule("child1", 2)
                .withSubmodule("child2", 4)

            lateinit var gradleRunner: DslGradleRunner

            afterEachTest {
                gradleRunner.setupProject()
                gradleRunner.runDetektTaskAndCheckResult { result ->
                    assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.NO_SOURCE)
                    projectLayout.submodules.forEach { submodule ->
                        assertThat(result.task(":${submodule.name}:detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                        assertThat(result.output).contains("number of classes: ${submodule.numberOfSourceFilesPerSourceDir}")
                    }

                    assertThat(projectFile("build/reports/detekt/detekt.xml")).doesNotExist()
                    assertThat(projectFile("build/reports/detekt/detekt.html")).doesNotExist()
                    projectLayout.submodules.forEach {
                        assertThat(projectFile("${it.name}/build/reports/detekt/detekt.xml")).exists()
                        assertThat(projectFile("${it.name}/build/reports/detekt/detekt.html")).exists()
                    }
                }
            }
            it("can be done using the groovy dsl") {

                val mainBuildFileContent: String = """
                |$GROOVY_PLUGINS_SECTION
                |allprojects {
                |   $REPOSITORIES_SECTION
                |}
                |subprojects {
                |   $GROOVY_APPLY_PLUGINS
                |}
                |""".trimMargin()

                gradleRunner = DslGradleRunner(projectLayout, "build.gradle", mainBuildFileContent)
            }
            it("can be done using the kotlin dsl") {

                val mainBuildFileContent: String = """
                |$KOTLIN_PLUGINS_SECTION
                |
                |allprojects {
                |   $REPOSITORIES_SECTION
                |}
                |subprojects {
                |   $KOTLIN_APPLY_PLUGINS
                |}
                |""".trimMargin()

                gradleRunner = DslGradleRunner(projectLayout, "build.gradle.kts", mainBuildFileContent)
            }
        }
        describe("is applied with defaults to main project and subprojects individually using the allprojects block") {
            val projectLayout = ProjectLayout(1)
                .withSubmodule("child1", 2)
                .withSubmodule("child2", 4)

            lateinit var gradleRunner: DslGradleRunner

            afterEachTest {
                gradleRunner.setupProject()
                gradleRunner.runDetektTaskAndCheckResult { result ->
                    assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                    projectLayout.submodules.forEach { submodule ->
                        assertThat(result.task(":${submodule.name}:detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                        assertThat(result.output).contains("number of classes: ${submodule.numberOfSourceFilesPerSourceDir}")
                    }

                    assertThat(projectFile("build/reports/detekt/detekt.xml")).exists()
                    assertThat(projectFile("build/reports/detekt/detekt.html")).exists()
                    projectLayout.submodules.forEach {
                        assertThat(projectFile("${it.name}/build/reports/detekt/detekt.xml")).exists()
                        assertThat(projectFile("${it.name}/build/reports/detekt/detekt.html")).exists()
                    }
                }
            }
            it("can be done using the groovy dsl") {

                val mainBuildFileContent: String = """
                |$GROOVY_PLUGINS_SECTION
                |
                |allprojects {
                |   $REPOSITORIES_SECTION
                |
                |   $GROOVY_APPLY_PLUGINS
                |}
                |""".trimMargin()

                gradleRunner = DslGradleRunner(projectLayout, "build.gradle", mainBuildFileContent)
            }
            it("can be done using the kotlin dsl") {

                val mainBuildFileContent: String = """
                |$KOTLIN_PLUGINS_SECTION
                |
                |allprojects {
                |   $REPOSITORIES_SECTION
                |
                |   $KOTLIN_APPLY_PLUGINS
                |}
                """.trimMargin()

                gradleRunner = DslGradleRunner(projectLayout, "build.gradle.kts", mainBuildFileContent)
            }
        }
        describe("uses custom configs when configured in allprojects block") {
            val projectLayout = ProjectLayout(1)
                .withSubmodule("child1", 2)
                .withSubmodule("child2", 4)

            lateinit var gradleRunner: DslGradleRunner

            afterEachTest {
                gradleRunner.setupProject()
                gradleRunner.runDetektTaskAndCheckResult { result ->
                    assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                    projectLayout.submodules.forEach { submodule ->
                        assertThat(result.task(":${submodule.name}:detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                    }

                    assertThat(projectFile("build/detekt-reports/detekt.xml")).exists()
                    assertThat(projectFile("build/detekt-reports/detekt.html")).exists()
                    projectLayout.submodules.forEach {
                        assertThat(projectFile("${it.name}/build/detekt-reports/detekt.xml")).exists()
                        assertThat(projectFile("${it.name}/build/detekt-reports/detekt.html")).exists()
                    }
                }
            }
            it("can be done using the groovy dsl") {

                val mainBuildFileContent: String = """
                |$GROOVY_PLUGINS_SECTION
                |
                |allprojects {
                |   $REPOSITORIES_SECTION
                |
                |   $GROOVY_APPLY_PLUGINS
                |
                |   detekt {
                |       reportsDir = file("build/detekt-reports")
                |   }
                |}
                """.trimMargin()

                gradleRunner = DslGradleRunner(projectLayout, "build.gradle", mainBuildFileContent)
            }
            it("can be done using the kotlin dsl") {

                val mainBuildFileContent: String = """
                |$KOTLIN_PLUGINS_SECTION
                |
                |allprojects {
                |   $REPOSITORIES_SECTION
                |
                |   $KOTLIN_APPLY_PLUGINS
                |
                |   detekt {
                |       reportsDir = file("build/detekt-reports")
                |   }
                |}
                """.trimMargin()

                gradleRunner = DslGradleRunner(projectLayout, "build.gradle.kts", mainBuildFileContent)
            }
        }
        describe("allows changing defaults in allprojects block that can be overwritten in subprojects") {
            val child2DetektConfig = """
                |detekt {
                |   reportsDir = file("build/custom")
                |}
                |""".trimMargin()
            val projectLayout = ProjectLayout(1)
                .withSubmodule("child1", 2)
                .withSubmodule("child2", 4, detektConfig = child2DetektConfig)

            lateinit var gradleRunner: DslGradleRunner

            afterEachTest {
                gradleRunner.setupProject()
                gradleRunner.runDetektTaskAndCheckResult { result ->
                    assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                    projectLayout.submodules.forEach { submodule ->
                        assertThat(result.task(":${submodule.name}:detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                    }

                    assertThat(projectFile("build/detekt-reports/detekt.xml")).exists()
                    assertThat(projectFile("build/detekt-reports/detekt.html")).exists()
                    assertThat(projectFile("child1/build/detekt-reports/detekt.xml")).exists()
                    assertThat(projectFile("child1/build/detekt-reports/detekt.html")).exists()
                    assertThat(projectFile("child2/build/custom/detekt.xml")).exists()
                    assertThat(projectFile("child2/build/custom/detekt.html")).exists()
                }
            }
            it("can be done using the groovy dsl") {

                val mainBuildFileContent: String = """
                |$GROOVY_PLUGINS_SECTION
                |
                |allprojects {
                |   $REPOSITORIES_SECTION
                |
                |   $GROOVY_APPLY_PLUGINS
                |
                |   detekt {
                |       reportsDir = file("build/detekt-reports")
                |   }
                |}
                """.trimMargin()

                gradleRunner = DslGradleRunner(projectLayout, "build.gradle", mainBuildFileContent)
            }
            it("can be done using the kotlin dsl") {

                val mainBuildFileContent: String = """
                |$KOTLIN_PLUGINS_SECTION
                |
                |allprojects {
                |   $REPOSITORIES_SECTION
                |
                |   $KOTLIN_APPLY_PLUGINS
                |
                |   detekt {
                |       reportsDir = file("build/detekt-reports")
                |   }
                |}
                """.trimMargin()

                gradleRunner = DslGradleRunner(projectLayout, "build.gradle.kts", mainBuildFileContent)
            }
        }
        listOf(groovy(), kotlin()).forEach { builder ->
            val projectLayout = ProjectLayout(1)
                .withSubmodule("child1", 2)
                .withSubmodule("child2", 4)

            val detektConfig: String = """
                |detekt {
                |    input = files("${"$"}projectDir/src", "${"$"}projectDir/child1/src", "${"$"}projectDir/child2/src")
                |}
                """.trimMargin()
            val gradleRunner = builder
                .withProjectLayout(projectLayout)
                .withDetektConfig(detektConfig)
                .build()

            describe("can be used in ${builder.gradleBuildName}") {
                it("can be applied to all files in entire project resulting in 1 report") {
                    gradleRunner.runDetektTaskAndCheckResult { result ->
                        assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                        projectLayout.submodules.forEach { submodule ->
                            assertThat(result.task(":${submodule.name}:detekt")).isNull()
                        }

                        assertThat(result.output).contains("number of classes: 7")

                        assertThat(projectFile("build/reports/detekt/detekt.xml")).exists()
                        assertThat(projectFile("build/reports/detekt/detekt.html")).exists()
                        projectLayout.submodules.forEach { submodule ->
                            assertThat(projectFile("${submodule.name}/build/reports/detekt/detekt.xml")).doesNotExist()
                            assertThat(projectFile("${submodule.name}/build/reports/detekt/detekt.html")).doesNotExist()
                        }
                    }
                }
            }
        }
    }
}) {
    companion object {
        private const val GROOVY_PLUGINS_SECTION = """
            |plugins {
            |   id "org.jetbrains.kotlin.jvm"
            |   id "io.gitlab.arturbosch.detekt"
            |}
            |"""

        private const val KOTLIN_PLUGINS_SECTION = """
            |plugins {
            |   kotlin("jvm")
            |   id("io.gitlab.arturbosch.detekt")
            |}
            |"""

        private const val REPOSITORIES_SECTION = """
            |repositories {
            |   mavenLocal()
            |   mavenCentral()
            |}
            |"""

        private const val GROOVY_APPLY_PLUGINS = """
            |apply plugin: "kotlin"
            |apply plugin: "io.gitlab.arturbosch.detekt"
            |"""

        private const val KOTLIN_APPLY_PLUGINS = """
            |plugins.apply("kotlin")
            |plugins.apply("io.gitlab.arturbosch.detekt")
            |"""
    }
}
