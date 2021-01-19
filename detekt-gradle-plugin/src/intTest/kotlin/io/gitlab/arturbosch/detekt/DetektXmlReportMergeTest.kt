package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.testkit.DslGradleRunner
import io.gitlab.arturbosch.detekt.testkit.DslTestBuilder
import io.gitlab.arturbosch.detekt.testkit.ProjectLayout
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

internal class DetektXmlReportMergeTest : Spek({

    describe("XML merge is configured correctly for multi module project") {

        val groovy = DslTestBuilder.groovy()
        val groovyBuildFileContent = """
            |${groovy.gradlePluginsSection}
            |
            |allprojects {
            |  ${groovy.gradleRepositoriesSection}
            |}
            |
            |task xmlReportMerge(type: io.gitlab.arturbosch.detekt.report.XmlReportMergeTask) {
            |  output = project.layout.buildDirectory.file("reports/detekt/merge.xml")
            |}
            |
            |subprojects {
            |  ${groovy.gradleApplyPlugins}
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
            |${kotlin.gradlePluginsSection}
            |
            |allprojects {
            |  ${kotlin.gradleRepositoriesSection}
            |}
            |
            |val xmlReportMerge by tasks.registering(io.gitlab.arturbosch.detekt.report.XmlReportMergeTask::class) {
            |  output.set(project.layout.buildDirectory.file("reports/detekt/merge.xml"))
            |}
            |
            |subprojects {
            |  ${kotlin.gradleApplyPlugins}
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
                val projectLayout = ProjectLayout(numberOfSourceFilesInRootPerSourceDir = 0)
                    .withSubmodule(
                        name = "child1",
                        numberOfSourceFilesPerSourceDir = 2,
                        numberOfCodeSmells = 2
                    )
                    .withSubmodule(
                        name = "child2",
                        numberOfSourceFilesPerSourceDir = 4,
                        numberOfCodeSmells = 4
                    )

                val gradleRunner = DslGradleRunner(projectLayout, builder.gradleBuildName, mainBuildFileContent)
                gradleRunner.setupProject()
                gradleRunner.runTasksAndExpectFailure("detekt", "xmlReportMerge", "--continue") { result ->
                    println(result.output)
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
