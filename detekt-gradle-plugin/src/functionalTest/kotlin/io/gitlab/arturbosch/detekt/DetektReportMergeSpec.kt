package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.testkit.DslGradleRunner
import io.gitlab.arturbosch.detekt.testkit.DslTestBuilder
import io.gitlab.arturbosch.detekt.testkit.ProjectLayout
import io.gitlab.arturbosch.detekt.testkit.reIndent
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DetektReportMergeSpec {
    @Test
    @Suppress("LongMethod")
    fun `Sarif merge is configured correctly for multi module project`() {
        val builder = DslTestBuilder.kotlin()
        val buildFileContent = """
            ${builder.gradlePlugins.reIndent()}
            
            allprojects {
                ${builder.gradleRepositories.reIndent(1)}
            }
            
            val sarifReportMerge by tasks.registering(io.gitlab.arturbosch.detekt.report.ReportMergeTask::class) {
                output.set(project.layout.buildDirectory.file("reports/detekt/merge.sarif"))
            }
            
            subprojects {
                ${builder.gradleSubprojectsApplyPlugins.reIndent(1)}
            
                sarifReportMerge {
                  input.from(tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().map { it.reports.sarif.outputLocation })
                }
            }
        """.trimIndent()

        val projectLayout = ProjectLayout(numberOfSourceFilesInRootPerSourceDir = 0).apply {
            addSubmodule(
                name = "child1",
                numberOfSourceFilesPerSourceDir = 2,
                numberOfCodeSmells = 2
            )
            addSubmodule(
                name = "child2",
                numberOfSourceFilesPerSourceDir = 4,
                numberOfCodeSmells = 4
            )
        }

        val gradleRunner = DslGradleRunner(projectLayout, builder.gradleBuildName, buildFileContent)
        gradleRunner.setupProject()
        gradleRunner.runTasksAndExpectFailure("detekt", "sarifReportMerge", "--continue") { result ->
            assertThat(result.output).contains("FAILURE: Build completed with 4 failures.")
            assertThat(result.output).containsIgnoringWhitespaces(
                """
                    Execution failed for task ':child1:detektMainSourceSet'.
                    > Analysis failed with 2 issues.
                """.trimIndent()
            )
            assertThat(result.output).containsIgnoringWhitespaces(
                """
                    Execution failed for task ':child2:detektMainSourceSet'.
                    > Analysis failed with 4 issues.
                """.trimIndent()
            )
            assertThat(projectFile("build/reports/detekt/mainSourceSet.sarif")).doesNotExist()
            assertThat(projectFile("build/reports/detekt/merge.sarif")).exists()
            assertThat(projectFile("build/reports/detekt/merge.sarif").readText())
                .contains("\"ruleId\": \"detekt.style.MagicNumber\"")
            projectLayout.submodules.forEach {
                assertThat(projectFile("${it.name}/build/reports/detekt/mainSourceSet.sarif")).exists()
            }
        }
    }

    @Test
    @Suppress("LongMethod")
    fun `XML merge is configured correctly for multi module project`() {
        val builder = DslTestBuilder.kotlin()
        val buildFileContent = """
            ${builder.gradlePlugins.reIndent()}
            
            allprojects {
                ${builder.gradleRepositories.reIndent(1)}
            }
            
            val xmlReportMerge by tasks.registering(io.gitlab.arturbosch.detekt.report.ReportMergeTask::class) {
                output.set(project.layout.buildDirectory.file("reports/detekt/merge.xml"))
            }
            
            subprojects {
                ${builder.gradleSubprojectsApplyPlugins.reIndent(1)}
            
                xmlReportMerge {
                    input.from(tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().map { it.reports.xml.outputLocation })
                }
            }
        """.trimIndent()

        val projectLayout = ProjectLayout(numberOfSourceFilesInRootPerSourceDir = 0).apply {
            addSubmodule(
                name = "child1",
                numberOfSourceFilesPerSourceDir = 2,
                numberOfCodeSmells = 2
            )
            addSubmodule(
                name = "child2",
                numberOfSourceFilesPerSourceDir = 4,
                numberOfCodeSmells = 4
            )
        }

        val gradleRunner = DslGradleRunner(projectLayout, builder.gradleBuildName, buildFileContent)
        gradleRunner.setupProject()
        gradleRunner.runTasksAndExpectFailure("detekt", "xmlReportMerge", "--continue") { result ->
            assertThat(result.output).contains("FAILURE: Build completed with 4 failures.")
            assertThat(result.output).containsIgnoringWhitespaces(
                """
                    Execution failed for task ':child1:detektMainSourceSet'.
                    > Analysis failed with 2 issues.
                """.trimIndent()
            )
            assertThat(result.output).containsIgnoringWhitespaces(
                """
                    Execution failed for task ':child2:detektMainSourceSet'.
                    > Analysis failed with 4 issues.
                """.trimIndent()
            )
            assertThat(projectFile("build/reports/detekt/mainSourceSet.xml")).doesNotExist()
            assertThat(projectFile("build/reports/detekt/merge.xml")).exists()
            assertThat(projectFile("build/reports/detekt/merge.xml").readText())
                .contains("<error column=\"31\" line=\"4\"")
            projectLayout.submodules.forEach {
                assertThat(projectFile("${it.name}/build/reports/detekt/mainSourceSet.xml")).exists()
            }
        }
    }
}
