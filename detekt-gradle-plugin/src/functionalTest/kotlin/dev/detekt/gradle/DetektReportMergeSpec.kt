package dev.detekt.gradle

import dev.detekt.gradle.testkit.DslGradleRunner
import dev.detekt.gradle.testkit.DslTestBuilder
import dev.detekt.gradle.testkit.ProjectLayout
import dev.detekt.gradle.testkit.reIndent
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test

class DetektReportMergeSpec {
    @Test
    @Suppress("LongMethod")
    fun `Sarif merge is configured correctly for multi module project`() {
        val builder = DslTestBuilder.kotlin()
        val buildFileContent = """
            ${builder.gradlePlugins.reIndent()}
            
            val sarifReportMerge = tasks.register<dev.detekt.gradle.report.ReportMergeTask>("sarifReportMerge") {
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
            configFileOrNone = "config/detekt/detekt.yml",
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
                .contains(""""ruleId": "detekt.style.MagicNumber"""")
            projectLayout.submodules.forEach {
                assertThat(projectFile("${it.name}/build/reports/detekt/detekt.sarif")).exists()
            }
        }
    }

    @Test
    @Suppress("LongMethod")
    fun `Checkstyle merge is configured correctly for multi module project`() {
        val builder = DslTestBuilder.kotlin()
        val buildFileContent = """
            ${builder.gradlePlugins.reIndent()}
            
            val checkstyleReportMerge = tasks.register<dev.detekt.gradle.report.ReportMergeTask>("checkstyleReportMerge") {
                output.set(project.layout.buildDirectory.file("reports/detekt/merge.xml"))
            }
            
            subprojects {
                ${builder.gradleSubprojectsApplyPlugins.reIndent(1)}
            
                checkstyleReportMerge {
                    input.from(tasks.withType<dev.detekt.gradle.Detekt>().map { it.reports.checkstyle.outputLocation })
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
            configFileOrNone = "config/detekt/detekt.yml",
        )
        gradleRunner.setupProject()
        gradleRunner.runTasksAndExpectFailure("detekt", "checkstyleReportMerge", "--continue") { result ->
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
                .contains("""<error column="31" line="4"""")
            projectLayout.submodules.forEach {
                assertThat(projectFile("${it.name}/build/reports/detekt/detekt.xml")).exists()
            }
        }
    }

    @Test
    @Suppress("LongMethod")
    fun `Custom report merge task which does not declare its dependency fails with a helpful validation problem`() {
        val builder = DslTestBuilder.kotlin()
        val buildFileContent = """
            ${builder.gradlePlugins.reIndent()}

            val customReportMerge = tasks.register<dev.detekt.gradle.report.ReportMergeTask>("customReportMerge") {
                output.set(project.layout.buildDirectory.file("reports/detekt/merge.xml"))
            }

            subprojects {
                ${builder.gradleSubprojectsApplyPlugins.reIndent(1)}

                tasks.withType<dev.detekt.gradle.Detekt>().configureEach {
                    reports {
                        custom {
                            reportId = "customXml"
                            outputLocation.set(project.layout.buildDirectory.file("reports/detekt/custom.xml"))
                        }
                    }
                }

                customReportMerge {
                    input.from(
                        tasks.withType<dev.detekt.gradle.Detekt>()
                            .map { it.reports.custom.first { it.reportId == "customXml" }.outputLocation }
                    )
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
            configFileOrNone = "config/detekt/detekt.yml",
        )
        gradleRunner.setupProject()
        gradleRunner.runTasksAndExpectFailure("detekt", "customReportMerge", "--continue") { result ->
            // Before the custom report locations were registered as task outputs, consuming one of them failed with
            // an IllegalStateException ("does not have a task associated with it") while scheduling the build.
            assertThat(result.output).doesNotContain("does not have a task associated with it")
            assertThat(result.output).contains("FAILURE: Build completed with 3 failures.")
            assertThat(result.output).contains(
                "uses this output of task ':child1:detekt' without declaring an explicit or implicit dependency"
            )
            assertThat(result.output).contains(
                "uses this output of task ':child2:detekt' without declaring an explicit or implicit dependency"
            )
        }
    }

    @Test
    @Suppress("LongMethod")
    fun `Reports including custom reports are merged when wired via the outputs of the detekt tasks`() {
        val builder = DslTestBuilder.kotlin()
        val buildFileContent = """
            ${builder.gradlePlugins.reIndent()}

            val customReportMerge = tasks.register<dev.detekt.gradle.report.ReportMergeTask>("customReportMerge") {
                output.set(project.layout.buildDirectory.file("reports/detekt/merge.xml"))
            }

            subprojects {
                ${builder.gradleSubprojectsApplyPlugins.reIndent(1)}

                tasks.withType<dev.detekt.gradle.Detekt>().configureEach {
                    reports {
                        custom {
                            reportId = "customXml"
                            outputLocation.set(project.layout.buildDirectory.file("reports/detekt/custom.xml"))
                        }
                    }
                }

                customReportMerge {
                    input.from(tasks.withType<dev.detekt.gradle.Detekt>().map { it.outputs.files })
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
            configFileOrNone = "config/detekt/detekt.yml",
        )
        gradleRunner.setupProject()
        gradleRunner.runTasksAndExpectFailure("detekt", "customReportMerge", "--continue") { result ->
            assertThat(result.output).contains("FAILURE: Build completed with 2 failures.")
            assertThat(result.output).doesNotContain("without declaring an explicit or implicit dependency")
            assertThat(result.task(":child1:detekt")?.outcome).isEqualTo(TaskOutcome.FAILED)
            assertThat(result.task(":child2:detekt")?.outcome).isEqualTo(TaskOutcome.FAILED)
            assertThat(result.task(":customReportMerge")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
            assertThat(projectFile("build/reports/detekt/merge.xml")).exists()
            assertThat(projectFile("build/reports/detekt/merge.xml").readText())
                .contains(""""version": "2.1.0"""")
        }
    }
}
