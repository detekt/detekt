package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.testkit.DslGradleRunner
import io.gitlab.arturbosch.detekt.testkit.DslTestBuilder
import io.gitlab.arturbosch.detekt.testkit.ProjectLayout
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DetektReportMergeSpec {
    @Test
    @Suppress("LongMethod")
    fun `Sarif merge is configured correctly for multi module project`() {
        val builder = DslTestBuilder.kotlin()
        val buildFileContent =
            """
                |${builder.gradlePlugins}
                |
                |allprojects {
                |  ${builder.gradleRepositories}
                |}
                |
                |val sarifReportMerge by tasks.registering(io.gitlab.arturbosch.detekt.report.ReportMergeTask::class) {
                |  output.set(project.layout.buildDirectory.file("reports/detekt/merge.sarif"))
                |}
                |
                |subprojects {
                |  ${builder.gradleSubprojectsApplyPlugins}
                |
                |  detekt {
                |    reports.sarif.enabled = true
                |  }
                |
                |  plugins.withType(io.gitlab.arturbosch.detekt.DetektPlugin::class) {
                |    tasks.withType(io.gitlab.arturbosch.detekt.Detekt::class) detekt@{
                |       sarifReportMerge.configure {
                |         this.mustRunAfter(this@detekt)
                |         input.from(this@detekt.sarifReportFile)
                |       }
                |    }
                |  }
                |}
                |
            """.trimMargin()

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
            assertThat(result.output).contains("FAILURE: Build completed with 2 failures.")
            assertThat(result.output).containsIgnoringWhitespaces(
                """
                    Execution failed for task ':child1:detekt'.
                    > Analysis failed with 2 weighted issues.
                """.trimIndent()
            )
            assertThat(result.output).containsIgnoringWhitespaces(
                """
                    Execution failed for task ':child2:detekt'.
                    > Analysis failed with 4 weighted issues.
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
            |${builder.gradlePlugins}
            |
            |allprojects {
            |  ${builder.gradleRepositories}
            |}
            |
            |val xmlReportMerge by tasks.registering(io.gitlab.arturbosch.detekt.report.ReportMergeTask::class) {
            |  output.set(project.layout.buildDirectory.file("reports/detekt/merge.xml"))
            |}
            |
            |subprojects {
            |  ${builder.gradleSubprojectsApplyPlugins}
            |
            |  detekt {
            |    reports.xml.enabled = true
            |  }
            |
            |  plugins.withType(io.gitlab.arturbosch.detekt.DetektPlugin::class) {
            |    tasks.withType(io.gitlab.arturbosch.detekt.Detekt::class) detekt@{
            |       xmlReportMerge.configure {
            |         this.mustRunAfter(this@detekt)
            |         input.from(this@detekt.xmlReportFile)
            |       }
            |    }
            |  }
            |}
            |
        """.trimMargin()

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
            assertThat(result.output).contains("FAILURE: Build completed with 2 failures.")
            assertThat(result.output).containsIgnoringWhitespaces(
                """
                    Execution failed for task ':child1:detekt'.
                    > Analysis failed with 2 weighted issues.
                """.trimIndent()
            )
            assertThat(result.output).containsIgnoringWhitespaces(
                """
                    Execution failed for task ':child2:detekt'.
                    > Analysis failed with 4 weighted issues.
                """.trimIndent()
            )
            assertThat(projectFile("build/reports/detekt/detekt.xml")).doesNotExist()
            assertThat(projectFile("build/reports/detekt/merge.xml")).exists()
            assertThat(projectFile("build/reports/detekt/merge.xml").readText())
                .contains("<error column=\"30\" line=\"4\"")
            projectLayout.submodules.forEach {
                assertThat(projectFile("${it.name}/build/reports/detekt/detekt.xml")).exists()
            }
        }
    }
}
