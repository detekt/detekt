package dev.detekt.gradle

import dev.detekt.gradle.testkit.DslGradleRunner
import dev.detekt.gradle.testkit.DslTestBuilder
import dev.detekt.gradle.testkit.ProjectLayout
import dev.detekt.gradle.testkit.reIndent
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DetektReportMergeSpec {
    @Test
    @Suppress("LongMethod")
    fun `Sarif merge is configured correctly for multi module project`() {
        val builder = DslTestBuilder.kotlin()
        val buildFileContent = """
            ${builder.gradlePlugins.reIndent()}
            
            val sarifReportMerge by tasks.registering(dev.detekt.gradle.report.ReportMergeTask::class) {
                output.set(project.layout.buildDirectory.file("reports/detekt/merge.sarif"))
            }
            
            subprojects {
                ${builder.gradleSubprojectsApplyPlugins.reIndent(1)}
            
                sarifReportMerge {
                  input.from(tasks.withType<dev.detekt.gradle.Detekt>().map { it.reports.sarif.outputLocation })
                }
            }
        """.trimIndent()

        val settingsFile = """
            dependencyResolutionManagement {
                ${builder.gradleRepositories.reIndent(1)}
            }
        """.trimIndent()

        val projectLayout = ProjectLayout(numberOfSourceFilesInRootPerSourceDir = 0).apply {
            addSubmodule(
                name = "child1",
                numberOfSourceFilesPerSourceDir = 2,
                numberOfFindings = 2
            )
            addSubmodule(
                name = "child2",
                numberOfSourceFilesPerSourceDir = 4,
                numberOfFindings = 4
            )
        }

        val gradleRunner = DslGradleRunner(
            projectLayout,
            builder.gradleBuildName,
            buildFileContent,
            settingsFile,
            disableIP = true,
        )
        gradleRunner.setupProject()
        gradleRunner.runTasksAndExpectFailure("detekt", "sarifReportMerge", "--continue") { result ->
            assertThat(result.output).contains("FAILURE: Build completed with 2 failures.")
            assertThat(result.output).containsIgnoringWhitespaces(
                """
                    Execution failed for task ':child1:detekt'.
                    > Analysis failed with 2 issues.
                """.trimIndent()
            )
            assertThat(result.output).containsIgnoringWhitespaces(
                """
                    Execution failed for task ':child2:detekt'.
                    > Analysis failed with 4 issues.
                """.trimIndent()
            )
            assertThat(projectFile("build/reports/detekt/detekt.sarif")).doesNotExist()
            assertThat(projectFile("build/reports/detekt/merge.sarif")).exists()
            assertThat(projectFile("build/reports/detekt/merge.sarif").readText())
                .contains("\"ruleId\": \"detekt.style.MagicNumber\"")
            projectLayout.submodules.forEach {
                assertThat(projectFile("${it.name}/build/reports/detekt/detekt.sarif")).exists()
            }
        }
    }

    @Test
    @Suppress("LongMethod")
    fun `XML merge is configured correctly for multi module project`() {
        val builder = DslTestBuilder.kotlin()
        val buildFileContent = """
            ${builder.gradlePlugins.reIndent()}
            
            val xmlReportMerge by tasks.registering(dev.detekt.gradle.report.ReportMergeTask::class) {
                output.set(project.layout.buildDirectory.file("reports/detekt/merge.xml"))
            }
            
            subprojects {
                ${builder.gradleSubprojectsApplyPlugins.reIndent(1)}
            
                xmlReportMerge {
                    input.from(tasks.withType<dev.detekt.gradle.Detekt>().map { it.reports.xml.outputLocation })
                }
            }
        """.trimIndent()

        val projectLayout = ProjectLayout(numberOfSourceFilesInRootPerSourceDir = 0).apply {
            addSubmodule(
                name = "child1",
                numberOfSourceFilesPerSourceDir = 2,
                numberOfFindings = 2
            )
            addSubmodule(
                name = "child2",
                numberOfSourceFilesPerSourceDir = 4,
                numberOfFindings = 4
            )
        }

        val settingsFile = """
            dependencyResolutionManagement {
                ${builder.gradleRepositories.reIndent(1)}
            }
        """.trimIndent()

        val gradleRunner = DslGradleRunner(
            projectLayout,
            builder.gradleBuildName,
            buildFileContent,
            settingsFile,
            disableIP = true,
        )
        gradleRunner.setupProject()
        gradleRunner.runTasksAndExpectFailure("detekt", "xmlReportMerge", "--continue") { result ->
            assertThat(result.output).contains("FAILURE: Build completed with 2 failures.")
            assertThat(result.output).containsIgnoringWhitespaces(
                """
                    Execution failed for task ':child1:detekt'.
                    > Analysis failed with 2 issues.
                """.trimIndent()
            )
            assertThat(result.output).containsIgnoringWhitespaces(
                """
                    Execution failed for task ':child2:detekt'.
                    > Analysis failed with 4 issues.
                """.trimIndent()
            )
            assertThat(projectFile("build/reports/detekt/detekt.xml")).doesNotExist()
            assertThat(projectFile("build/reports/detekt/merge.xml")).exists()
            assertThat(projectFile("build/reports/detekt/merge.xml").readText())
                .contains("<error column=\"31\" line=\"4\"")
            projectLayout.submodules.forEach {
                assertThat(projectFile("${it.name}/build/reports/detekt/detekt.xml")).exists()
            }
        }
    }
}
