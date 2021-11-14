package io.gitlab.arturbosch.detekt.report

import io.gitlab.arturbosch.detekt.getJdkVersion
import io.gitlab.arturbosch.detekt.manifestContent
import io.gitlab.arturbosch.detekt.testkit.DslGradleRunner
import io.gitlab.arturbosch.detekt.testkit.DslTestBuilder
import io.gitlab.arturbosch.detekt.testkit.ProjectLayout
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ReportMergeSpec : Spek({

    describe("Merging reports in a multi module projects") {

        it("for jvm detekt") {
            val builder = DslTestBuilder.groovy()
            val projectLayout = ProjectLayout(0).apply {
                addSubmodule(
                    "child1",
                    numberOfSourceFilesPerSourceDir = 2,
                    buildFileContent = """
                        ${builder.gradleSubprojectsApplyPlugins}
                        |apply plugin: 'java-library'
                    """.trimMargin()
                )
                addSubmodule(
                    "child2",
                    numberOfSourceFilesPerSourceDir = 2,
                    buildFileContent = """
                        ${builder.gradleSubprojectsApplyPlugins}
                        |apply plugin: 'java-library'
                    """.trimMargin()
                )
            }
            val mainBuildFileContent: String = """
                |plugins {
                |    id "io.gitlab.arturbosch.detekt" apply false
                |}
                |
                |allprojects {
                |    ${builder.gradleRepositories}
                |}
                |
                |task reportMerge(type: io.gitlab.arturbosch.detekt.report.ReportMergeTask) {
                |    output = project.layout.buildDirectory.file("reports/detekt/merge.xml")
                |    outputs.cacheIf { false }
                |    outputs.upToDateWhen { false }
                |}
                |
                |subprojects {
                |    apply plugin: "org.jetbrains.kotlin.jvm"
                |    apply plugin: "io.gitlab.arturbosch.detekt"
                |
                |    detekt {
                |        reports.xml.enabled = true
                |    }
                |    
                |    plugins.withType(io.gitlab.arturbosch.detekt.DetektPlugin) {
                |        tasks.withType(io.gitlab.arturbosch.detekt.Detekt).configureEach { detektTask ->
                |            finalizedBy(reportMerge)
                |            reportMerge.configure { mergeTask -> mergeTask.input.from(detektTask.xmlReportFile) }
                |        }
                |    }
                |}""".trimMargin()

            val gradleRunner = DslGradleRunner(
                projectLayout = projectLayout,
                buildFileName = builder.gradleBuildName,
                mainBuildFileContent = mainBuildFileContent
            )

            gradleRunner.setupProject()
            gradleRunner.runTasksAndCheckResult("detektMain", "reportMerge", "--continue") { result ->
                projectLayout.submodules.forEach { submodule ->
                    assertThat(result.task(":${submodule.name}:detektMain")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                }

                projectLayout.submodules.forEach {
                    assertThat(projectFile("${it.name}/build/reports/detekt/main.xml")).exists()
                }
                // #4192 this should exist by default
                assertThat(projectFile("build/reports/detekt/merge.xml")).doesNotExist()
            }
        }

        it("for android detekt") {
            val builder = DslTestBuilder.groovy()
            val projectLayout = ProjectLayout(0).apply {
                addSubmodule(
                    name = "app",
                    numberOfSourceFilesPerSourceDir = 1,
                    buildFileContent = """
                        plugins {
                            id "com.android.application"
                            id "kotlin-android"
                            id "io.gitlab.arturbosch.detekt"
                        }
                        android {
                           compileSdkVersion 30
                        }
                        dependencies {
                            implementation project(":lib")
                        }
                    """.trimIndent(),
                    srcDirs = listOf("src/main/java", "src/debug/java", "src/test/java", "src/androidTest/java"),
                )
                addSubmodule(
                    name = "lib",
                    numberOfSourceFilesPerSourceDir = 1,
                    buildFileContent = """
                        plugins {
                            id "com.android.library"
                            id "kotlin-android"
                        }
                        android {
                           compileSdkVersion 30
                        }
                    """.trimIndent(),
                    srcDirs = listOf("src/main/java", "src/debug/java", "src/test/java", "src/androidTest/java")
                )
            }
            val mainBuildFileContent: String = """
                |plugins {
                |    id "io.gitlab.arturbosch.detekt" apply false
                |}
                |
                |allprojects {
                |    repositories {
                |        mavenCentral()
                |        google()
                |        mavenLocal()
                |    }
                |}
                |
                |task reportMerge(type: io.gitlab.arturbosch.detekt.report.ReportMergeTask) {
                |    output = project.layout.buildDirectory.file("reports/detekt/merge.xml")
                |    outputs.cacheIf { false }
                |    outputs.upToDateWhen { false }
                |}
                |
                |subprojects {
                |    apply plugin: "io.gitlab.arturbosch.detekt"
                |
                |    detekt {
                |        reports.xml.enabled = true
                |    }
                |    
                |    plugins.withType(io.gitlab.arturbosch.detekt.DetektPlugin) {
                |        tasks.withType(io.gitlab.arturbosch.detekt.Detekt).configureEach { detektTask ->
                |            finalizedBy(reportMerge)
                |            reportMerge.configure { mergeTask -> mergeTask.input.from(detektTask.xmlReportFile) }
                |        }
                |    }
                |}""".trimMargin()

            val jvmArgs = if (getJdkVersion() < 9) {
                "-Xmx2g -XX:MaxMetaspaceSize=1g"
            } else {
                // Manifest merger uses a reflective GSON API https://issuetracker.google.com/issues/193919814
                "-Xmx2g -XX:MaxMetaspaceSize=1g --add-opens=java.base/java.io=ALL-UNNAMED"
            }

            val gradleRunner = DslGradleRunner(
                projectLayout = projectLayout,
                buildFileName = builder.gradleBuildName,
                mainBuildFileContent = mainBuildFileContent,
                jvmArgs = jvmArgs
            )

            gradleRunner.setupProject()
            gradleRunner.writeProjectFile(
                "app/src/main/AndroidManifest.xml",
                manifestContent("io.github.detekt.app")
            )
            gradleRunner.writeProjectFile(
                "lib/src/main/AndroidManifest.xml",
                manifestContent("io.github.detekt.lib")
            )
            gradleRunner.runTasksAndCheckResult("detektMain", "reportMerge", "--continue") { result ->
                projectLayout.submodules.forEach { submodule ->
                    assertThat(result.task(":${submodule.name}:detektMain")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                }

                projectLayout.submodules.forEach {
                    assertThat(projectFile("${it.name}/build/reports/detekt/debug.xml")).exists()
                }
                // #4192 this should exist by default
                assertThat(projectFile("build/reports/detekt/merge.xml")).doesNotExist()
            }
        }
    }
})
