package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.testkit.DslGradleRunner
import io.gitlab.arturbosch.detekt.testkit.DslTestBuilder
import io.gitlab.arturbosch.detekt.testkit.ProjectLayout
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

internal class DetektReportMergeTest : Spek({

    describe("Sarif merge is configured correctly for multi module project") {

        val groovy = DslTestBuilder.groovy()
        val groovyBuildFileContent = """
            |${groovy.gradlePlugins}
            |
            |allprojects {
            |  ${groovy.gradleRepositories}
            |}
            |
            |task sarifReportMerge(type: io.gitlab.arturbosch.detekt.report.ReportMergeTask) {
            |  output = project.layout.buildDirectory.file("reports/detekt/merge.sarif")
            |}
            |
            |subprojects {
            |  ${groovy.gradleSubprojectsApplyPlugins}
            |  
            |  detekt {
            |    reports.sarif.enabled = true
            |  }
            |  
            |  plugins.withType(io.gitlab.arturbosch.detekt.DetektPlugin) {
            |    tasks.withType(io.gitlab.arturbosch.detekt.Detekt) { detektTask ->
            |       sarifReportMerge.configure { mergeTask ->
            |         mergeTask.mustRunAfter(detektTask)
            |         mergeTask.input.from(detektTask.sarifReportFile)
            |       }
            |    }
            |  }
            |}
            |""".trimMargin()
        val kotlin = DslTestBuilder.kotlin()
        val kotlinBuildFileContent = """
            |${kotlin.gradlePlugins}
            |
            |allprojects {
            |  ${kotlin.gradleRepositories}
            |}
            |
            |val sarifReportMerge by tasks.registering(io.gitlab.arturbosch.detekt.report.ReportMergeTask::class) {
            |  output.set(project.layout.buildDirectory.file("reports/detekt/merge.sarif"))
            |}
            |
            |subprojects {
            |  ${kotlin.gradleSubprojectsApplyPlugins}
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
            |""".trimMargin()

        listOf(
            groovy to groovyBuildFileContent,
            kotlin to kotlinBuildFileContent
        ).forEach { (builder, mainBuildFileContent) ->
            it("using ${builder.gradleBuildName}") {
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

                val gradleRunner = DslGradleRunner(projectLayout, builder.gradleBuildName, mainBuildFileContent)
                gradleRunner.setupProject()
                gradleRunner.runTasksAndExpectFailure("detekt", "sarifReportMerge", "--continue") { _ ->
                    assertThat(projectFile("build/reports/detekt/detekt.sarif")).doesNotExist()
                    assertThat(projectFile("build/reports/detekt/merge.sarif")).exists()
                    assertThat(projectFile("build/reports/detekt/merge.sarif").readText())
                        .contains("\"ruleId\": \"detekt.style.MagicNumber\"")
                    projectLayout.submodules.forEach {
                        assertThat(projectFile("${it.name}/build/reports/detekt/detekt.sarif")).exists()
                    }
                }
            }
        }
    }

    describe("XML merge is configured correctly for multi module project") {

        val groovy = DslTestBuilder.groovy()
        val groovyBuildFileContent = """
            |${groovy.gradlePlugins}
            |
            |allprojects {
            |  ${groovy.gradleRepositories}
            |}
            |
            |task xmlReportMerge(type: io.gitlab.arturbosch.detekt.report.ReportMergeTask) {
            |  output = project.layout.buildDirectory.file("reports/detekt/merge.xml")
            |}
            |
            |subprojects {
            |  ${groovy.gradleSubprojectsApplyPlugins}
            |  
            |  detekt {
            |    reports.xml.enabled = true
            |  }
            |  
            |  plugins.withType(io.gitlab.arturbosch.detekt.DetektPlugin) {
            |    tasks.withType(io.gitlab.arturbosch.detekt.Detekt) { detektTask ->
            |       xmlReportMerge.configure { mergeTask ->
            |         mergeTask.mustRunAfter(detektTask)
            |         mergeTask.input.from(detektTask.xmlReportFile)
            |       }
            |    }
            |  }
            |}
            |""".trimMargin()
        val kotlin = DslTestBuilder.kotlin()
        val kotlinBuildFileContent = """
            |${kotlin.gradlePlugins}
            |
            |allprojects {
            |  ${kotlin.gradleRepositories}
            |}
            |
            |val xmlReportMerge by tasks.registering(io.gitlab.arturbosch.detekt.report.ReportMergeTask::class) {
            |  output.set(project.layout.buildDirectory.file("reports/detekt/merge.xml"))
            |}
            |
            |subprojects {
            |  ${kotlin.gradleSubprojectsApplyPlugins}
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
            |""".trimMargin()

        listOf(
            groovy to groovyBuildFileContent,
            kotlin to kotlinBuildFileContent
        ).forEach { (builder, mainBuildFileContent) ->
            it("using ${builder.gradleBuildName}") {
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

                val gradleRunner = DslGradleRunner(projectLayout, builder.gradleBuildName, mainBuildFileContent)
                gradleRunner.setupProject()
                gradleRunner.runTasksAndExpectFailure("detekt", "xmlReportMerge", "--continue") { _ ->
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
    }
})
