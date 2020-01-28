package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.DslTestBuilder.Companion.groovy
import io.gitlab.arturbosch.detekt.DslTestBuilder.Companion.kotlin
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

internal class DetektTaskMultiModuleIntegrationTest : Spek({

    describe("The Detekt Gradle plugin used in a multi module project") {

        listOf(groovy(), kotlin()).forEach { builder ->

            describe("using ${builder.gradleBuildName}") {

                it("""
                    |is applied with defaults to all subprojects individually
                    |without sources in root project using the subprojects block
                """.trimMargin()) {
                    val projectLayout = ProjectLayout(0)
                        .withSubmodule("child1", 2)
                        .withSubmodule("child2", 4)

                    val mainBuildFileContent: String = """
                        |${builder.gradlePluginsSection}
                        |
                        |allprojects {
                        |   ${builder.gradleRepositoriesSection}
                        |}
                        |subprojects {
                        |   ${builder.gradleApplyPlugins}
                        |}
                        |""".trimMargin()

                    val gradleRunner = DslGradleRunner(projectLayout, builder.gradleBuildName, mainBuildFileContent)

                    gradleRunner.setupProject()
                    gradleRunner.runDetektTaskAndCheckResult { result ->
                        assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.NO_SOURCE)
                        projectLayout.submodules.forEach { submodule ->
                            assertThat(result.task(":${submodule.name}:detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                        }

                        assertThat(projectFile("build/reports/detekt/detekt.xml")).doesNotExist()
                        assertThat(projectFile("build/reports/detekt/detekt.html")).doesNotExist()
                        assertThat(projectFile("build/reports/detekt/detekt.txt")).doesNotExist()
                        projectLayout.submodules.forEach {
                            assertThat(projectFile("${it.name}/build/reports/detekt/detekt.xml")).exists()
                            assertThat(projectFile("${it.name}/build/reports/detekt/detekt.html")).exists()
                            assertThat(projectFile("${it.name}/build/reports/detekt/detekt.txt")).exists()
                        }
                    }
                }

                it("""
                    |is applied with defaults to main project
                    |and subprojects individually using the allprojects block
                """.trimMargin()) {
                    val projectLayout = ProjectLayout(1)
                        .withSubmodule("child1", 2)
                        .withSubmodule("child2", 4)

                    val mainBuildFileContent: String = """
                        |${builder.gradlePluginsSection}
                        |
                        |allprojects {
                        |   ${builder.gradleRepositoriesSection}
                        |   ${builder.gradleApplyPlugins}
                        |}
                        |""".trimMargin()

                    val gradleRunner = DslGradleRunner(projectLayout, builder.gradleBuildName, mainBuildFileContent)

                    gradleRunner.setupProject()
                    gradleRunner.runDetektTaskAndCheckResult { result ->
                        assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                        projectLayout.submodules.forEach { submodule ->
                            assertThat(result.task(":${submodule.name}:detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                        }

                        assertThat(projectFile("build/reports/detekt/detekt.xml")).exists()
                        assertThat(projectFile("build/reports/detekt/detekt.html")).exists()
                        assertThat(projectFile("build/reports/detekt/detekt.txt")).exists()
                        projectLayout.submodules.forEach {
                            assertThat(projectFile("${it.name}/build/reports/detekt/detekt.xml")).exists()
                            assertThat(projectFile("${it.name}/build/reports/detekt/detekt.html")).exists()
                            assertThat(projectFile("${it.name}/build/reports/detekt/detekt.txt")).exists()
                        }
                    }
                }

                it("uses custom configs when configured in allprojects block") {
                    val projectLayout = ProjectLayout(1)
                        .withSubmodule("child1", 2)
                        .withSubmodule("child2", 4)

                    val mainBuildFileContent: String = """
                        |${builder.gradlePluginsSection}
                        |
                        |allprojects {
                        |   ${builder.gradleRepositoriesSection}
                        |   ${builder.gradleApplyPlugins}
                        |
                        |   detekt {
                        |       reportsDir = file("build/detekt-reports")
                        |   }
                        |}
                        |""".trimMargin()

                    val gradleRunner = DslGradleRunner(projectLayout, builder.gradleBuildName, mainBuildFileContent)
                    gradleRunner.setupProject()
                    gradleRunner.runDetektTaskAndCheckResult { result ->
                        assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                        projectLayout.submodules.forEach { submodule ->
                            assertThat(result.task(":${submodule.name}:detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                        }

                        assertThat(projectFile("build/detekt-reports/detekt.xml")).exists()
                        assertThat(projectFile("build/detekt-reports/detekt.html")).exists()
                        assertThat(projectFile("build/detekt-reports/detekt.txt")).exists()
                        projectLayout.submodules.forEach {
                            assertThat(projectFile("${it.name}/build/detekt-reports/detekt.xml")).exists()
                            assertThat(projectFile("${it.name}/build/detekt-reports/detekt.html")).exists()
                            assertThat(projectFile("${it.name}/build/detekt-reports/detekt.txt")).exists()
                        }
                    }
                }

                it("allows changing defaults in allprojects block that can be overwritten in subprojects") {
                    val child2DetektConfig = """
                        |detekt {
                        |   reportsDir = file("build/custom")
                        |}
                        |""".trimMargin()

                    val projectLayout = ProjectLayout(1)
                        .withSubmodule("child1", 2)
                        .withSubmodule("child2", 4, detektConfig = child2DetektConfig)

                    val mainBuildFileContent: String = """
                        |${builder.gradlePluginsSection}
                        |
                        |allprojects {
                        |   ${builder.gradleRepositoriesSection}
                        |   ${builder.gradleApplyPlugins}
                        |
                        |   detekt {
                        |       reportsDir = file("build/detekt-reports")
                        |   }
                        |}
                        |""".trimMargin()

                    val gradleRunner = DslGradleRunner(projectLayout, builder.gradleBuildName, mainBuildFileContent)

                    gradleRunner.setupProject()
                    gradleRunner.runDetektTaskAndCheckResult { result ->
                        assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                        projectLayout.submodules.forEach { submodule ->
                            assertThat(result.task(":${submodule.name}:detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                        }

                        assertThat(projectFile("build/detekt-reports/detekt.xml")).exists()
                        assertThat(projectFile("build/detekt-reports/detekt.html")).exists()
                        assertThat(projectFile("build/detekt-reports/detekt.txt")).exists()
                        assertThat(projectFile("child1/build/detekt-reports/detekt.xml")).exists()
                        assertThat(projectFile("child1/build/detekt-reports/detekt.html")).exists()
                        assertThat(projectFile("child1/build/detekt-reports/detekt.txt")).exists()
                        assertThat(projectFile("child2/build/custom/detekt.xml")).exists()
                        assertThat(projectFile("child2/build/custom/detekt.html")).exists()
                        assertThat(projectFile("child2/build/custom/detekt.txt")).exists()
                    }
                }

                it("can be applied to all files in entire project resulting in 1 report") {
                    val projectLayout = ProjectLayout(1)
                        .withSubmodule("child1", 2)
                        .withSubmodule("child2", 4)

                    val detektConfig: String = """
                        |detekt {
                        |    input = files(
                        |       "${"$"}projectDir/src",
                        |       "${"$"}projectDir/child1/src",
                        |       "${"$"}projectDir/child2/src"
                        |    )
                        |}
                        """.trimMargin()
                    val gradleRunner = builder
                        .withProjectLayout(projectLayout)
                        .withDetektConfig(detektConfig)
                        .build()

                    gradleRunner.runDetektTaskAndCheckResult { result ->
                        assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                        projectLayout.submodules.forEach { submodule ->
                            assertThat(result.task(":${submodule.name}:detekt")).isNull()
                        }

                        assertThat(projectFile("build/reports/detekt/detekt.xml")).exists()
                        assertThat(projectFile("build/reports/detekt/detekt.html")).exists()
                        assertThat(projectFile("build/reports/detekt/detekt.txt")).exists()
                        projectLayout.submodules.forEach { submodule ->
                            assertThat(projectFile("${submodule.name}/build/reports/detekt/detekt.xml")).doesNotExist()
                            assertThat(projectFile("${submodule.name}/build/reports/detekt/detekt.html")).doesNotExist()
                            assertThat(projectFile("${submodule.name}/build/reports/detekt/detekt.txt")).doesNotExist()
                        }
                    }
                }
            }
        }
    }
})
