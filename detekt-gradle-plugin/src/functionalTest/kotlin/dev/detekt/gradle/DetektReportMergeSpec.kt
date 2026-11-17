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
    fun `Consuming a custom report output location without declaring a dependency does not fail while scheduling the build`() {
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
            // When custom report locations were declared as output properties, consuming one of them failed with an
            // IllegalStateException ("does not have a task associated with it") while scheduling the build. They are
            // no longer output properties, so the build is scheduled and only the detekt tasks fail (they fail
            // because the source files contain findings). The merge task may run before or after the detekt tasks as
            // no dependency is declared - the wiring shown here is intentionally incomplete.
            assertThat(result.output).doesNotContain("does not have a task associated with it")
            assertThat(result.output).doesNotContain("without declaring an explicit or implicit dependency")
            assertThat(result.output).contains("FAILURE: Build completed with 2 failures.")
            assertThat(result.task(":child1:detekt")?.outcome).isEqualTo(TaskOutcome.FAILED)
            assertThat(result.task(":child2:detekt")?.outcome).isEqualTo(TaskOutcome.FAILED)
            assertThat(result.task(":customReportMerge")?.outcome)
                .isIn(TaskOutcome.SUCCESS, TaskOutcome.FROM_CACHE)
        }
    }

    @Test
    @Suppress("LongMethod")
    fun `Custom reports are merged when the merge task declares explicit ordering on the detekt tasks`() {
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
                    // Custom report locations do not carry an implicit task dependency (they are not task outputs),
                    // so the ordering on the detekt tasks is declared explicitly. Locations of default reports such
                    // as `reports.checkstyle.outputLocation` create the dependency implicitly. `mustRunAfter` keeps
                    // the merge task running when the detekt tasks fail (they fail because the source files contain
                    // findings), which is what the documented `--continue` merging pattern relies on.
                    mustRunAfter(tasks.withType<dev.detekt.gradle.Detekt>())
                    input.from(tasks.withType<dev.detekt.gradle.Detekt>().map { it.reports.checkstyle.outputLocation })
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
            assertThat(result.output).contains("FAILURE: Build completed with 2 failures.")
            assertThat(result.output).doesNotContain("does not have a task associated with it")
            assertThat(result.task(":child1:detekt")?.outcome).isEqualTo(TaskOutcome.FAILED)
            assertThat(result.task(":child2:detekt")?.outcome).isEqualTo(TaskOutcome.FAILED)
            assertThat(result.task(":customReportMerge")?.outcome)
                .isIn(TaskOutcome.SUCCESS, TaskOutcome.FROM_CACHE)
            assertThat(projectFile("build/reports/detekt/merge.xml")).exists()
            assertThat(projectFile("build/reports/detekt/merge.xml").readText())
                .contains("""<error column="31" line="4"""")
            projectLayout.submodules.forEach {
                assertThat(projectFile("${it.name}/build/reports/detekt/custom.xml")).doesNotExist()
            }
        }
    }
}
