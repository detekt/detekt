package io.gitlab.arturbosch.detekt.report

import io.gitlab.arturbosch.detekt.manifestContent
import io.gitlab.arturbosch.detekt.testkit.DslGradleRunner
import io.gitlab.arturbosch.detekt.testkit.DslTestBuilder
import io.gitlab.arturbosch.detekt.testkit.ProjectLayout
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledForJreRange
import org.junit.jupiter.api.condition.EnabledIf
import org.junit.jupiter.api.condition.JRE.JAVA_11

class ReportMergeSpec {

    @Suppress("LongMethod")
    @Test
    fun `for jvm detekt`() {
        val builder = DslTestBuilder.kotlin()
        val projectLayout = ProjectLayout(0).apply {
            addSubmodule(
                "child1",
                numberOfSourceFilesPerSourceDir = 2,
                buildFileContent = """
                    ${builder.gradleSubprojectsApplyPlugins}
                    |plugins.apply("java-library")
                """.trimMargin()
            )
            addSubmodule(
                "child2",
                numberOfSourceFilesPerSourceDir = 2,
                buildFileContent = """
                    ${builder.gradleSubprojectsApplyPlugins}
                    |plugins.apply("java-library")
                """.trimMargin()
            )
        }
        val mainBuildFileContent: String = """
            |plugins {
            |    id("io.gitlab.arturbosch.detekt")
            |}
            |
            |allprojects {
            |    ${builder.gradleRepositories}
            |}
            |
            |val reportMerge by tasks.registering(io.gitlab.arturbosch.detekt.report.ReportMergeTask::class) {
            |    output.set(project.layout.buildDirectory.file("reports/detekt/merge.xml"))
            |    outputs.cacheIf { false }
            |    outputs.upToDateWhen { false }
            |}
            |
            |subprojects {
            |    apply(plugin = "org.jetbrains.kotlin.jvm")
            |    apply(plugin = "io.gitlab.arturbosch.detekt")
            |
            |    detekt {
            |        reports.xml.enabled = true
            |    }
            |    
            |    plugins.withType<io.gitlab.arturbosch.detekt.DetektPlugin> {
            |        tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
            |            finalizedBy(reportMerge)
            |            reportMerge.configure { input.from(xmlReportFile) }
            |        }
            |    }
            |}
        """.trimMargin()

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

            assertThat(projectFile("build/reports/detekt/merge.xml")).exists()
        }
    }

    @Suppress("LongMethod")
    @Test
    @EnabledForJreRange(min = JAVA_11, disabledReason = "Android Gradle Plugin 7.0+ requires JDK 11 or newer")
    @EnabledIf("io.gitlab.arturbosch.detekt.DetektAndroidSpecKt#isAndroidSdkInstalled")
    fun `for android detekt`() {
        val builder = DslTestBuilder.kotlin()
        val projectLayout = ProjectLayout(0).apply {
            addSubmodule(
                name = "app",
                numberOfSourceFilesPerSourceDir = 1,
                buildFileContent = """
                    plugins {
                        id("com.android.application")
                        kotlin("android")
                        id("io.gitlab.arturbosch.detekt")
                    }
                    android {
                       compileSdkVersion(30)
                    }
                    dependencies {
                        implementation(project(":lib"))
                    }
                """.trimIndent(),
                srcDirs = listOf("src/main/java", "src/debug/java", "src/test/java", "src/androidTest/java"),
            )
            addSubmodule(
                name = "lib",
                numberOfSourceFilesPerSourceDir = 1,
                buildFileContent = """
                    plugins {
                        id("com.android.library")
                        kotlin("android")
                    }
                    android {
                       compileSdkVersion(30)
                    }
                """.trimIndent(),
                srcDirs = listOf("src/main/java", "src/debug/java", "src/test/java", "src/androidTest/java")
            )
        }
        val mainBuildFileContent: String = """
            |plugins {
            |    id("io.gitlab.arturbosch.detekt")
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
            |val reportMerge by tasks.registering(io.gitlab.arturbosch.detekt.report.ReportMergeTask::class) {
            |    output.set(project.layout.buildDirectory.file("reports/detekt/merge.xml"))
            |    outputs.cacheIf { false }
            |    outputs.upToDateWhen { false }
            |}
            |
            |subprojects {
            |    apply(plugin = "io.gitlab.arturbosch.detekt")
            |
            |    detekt {
            |        reports.xml.enabled = true
            |    }
            |    
            |    plugins.withType<io.gitlab.arturbosch.detekt.DetektPlugin> {
            |        tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
            |            finalizedBy(reportMerge)
            |            reportMerge.configure { input.from(xmlReportFile) }
            |        }
            |    }
            |}
        """.trimMargin()

        val jvmArgs = "-Xmx2g -XX:MaxMetaspaceSize=1g"

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
