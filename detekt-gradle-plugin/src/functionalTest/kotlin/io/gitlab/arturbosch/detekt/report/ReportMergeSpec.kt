package io.gitlab.arturbosch.detekt.report

import io.gitlab.arturbosch.detekt.manifestContent
import io.gitlab.arturbosch.detekt.testkit.DslGradleRunner
import io.gitlab.arturbosch.detekt.testkit.DslTestBuilder
import io.gitlab.arturbosch.detekt.testkit.ProjectLayout
import io.gitlab.arturbosch.detekt.testkit.reIndent
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIf

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
                    ${builder.gradleSubprojectsApplyPlugins.reIndent(baseIndent = 5)}
                """.trimIndent()
            )
            addSubmodule(
                "child2",
                numberOfSourceFilesPerSourceDir = 2,
                buildFileContent = """
                    ${builder.gradleSubprojectsApplyPlugins.reIndent(baseIndent = 5)}
                """.trimIndent()
            )
        }
        val mainBuildFileContent: String = """
            plugins {
                id("io.gitlab.arturbosch.detekt")
            }
            
            val reportMerge by tasks.registering(io.gitlab.arturbosch.detekt.report.ReportMergeTask::class) {
                output.set(project.layout.buildDirectory.file("reports/detekt/merge.xml"))
                outputs.cacheIf { false }
                outputs.upToDateWhen { false }
            }
            
            subprojects {
                apply(plugin = "org.jetbrains.kotlin.jvm")
                apply(plugin = "io.gitlab.arturbosch.detekt")
            
                plugins.withType<io.gitlab.arturbosch.detekt.DetektPlugin> {
                    tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
                        reportMerge.configure { input.from(reports.xml.outputLocation) }
                    }
                }
            }
        """.trimIndent()

        val settingsFile = """
            dependencyResolutionManagement {
                ${builder.gradleRepositories.reIndent(1)}
            }
        """.trimIndent()

        val gradleRunner = DslGradleRunner(
            projectLayout = projectLayout,
            buildFileName = builder.gradleBuildName,
            mainBuildFileContent = mainBuildFileContent,
            settingsContent = settingsFile,
            disableIP = true,
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
                       compileSdk = 34
                       namespace = "io.github.detekt.app"
                       compileOptions {
                           sourceCompatibility = JavaVersion.VERSION_11
                           targetCompatibility = JavaVersion.VERSION_11
                       }
                    }
                    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
                        compilerOptions {
                            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
                        }
                    }
                    dependencies {
                        implementation(project(":lib"))
                    }
                """.trimIndent(),
                srcDirs = listOf("src/main/java", "src/debug/java", "src/test/java", "src/androidUnitTest/java"),
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
                        compileSdk = 34
                        namespace = "io.github.detekt.lib"
                        compileOptions {
                            sourceCompatibility = JavaVersion.VERSION_11
                            targetCompatibility = JavaVersion.VERSION_11
                        }
                    }
                    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
                        compilerOptions {
                            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
                        }
                    }
                """.trimIndent(),
                srcDirs = listOf("src/main/java", "src/debug/java", "src/test/java", "src/androidUnitTest/java")
            )
        }
        val mainBuildFileContent: String = """
            plugins {
                id("io.gitlab.arturbosch.detekt")
            }
            
            val reportMerge by tasks.registering(io.gitlab.arturbosch.detekt.report.ReportMergeTask::class) {
                output.set(project.layout.buildDirectory.file("reports/detekt/merge.xml"))
                outputs.cacheIf { false }
                outputs.upToDateWhen { false }
            }
            
            subprojects {
                apply(plugin = "io.gitlab.arturbosch.detekt")
            
                plugins.withType<io.gitlab.arturbosch.detekt.DetektPlugin> {
                    tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
                        reportMerge.configure { input.from(reports.xml.outputLocation) }
                    }
                }
            }
        """.trimIndent()

        val settingsFile = """
            dependencyResolutionManagement {
                repositories {
                    mavenLocal()
                    mavenCentral()
                    google()
                }
            }
        """.trimIndent()

        val jvmArgs = "-Xmx2g -XX:MaxMetaspaceSize=1g"

        val gradleRunner = DslGradleRunner(
            projectLayout = projectLayout,
            buildFileName = builder.gradleBuildName,
            mainBuildFileContent = mainBuildFileContent,
            settingsContent = settingsFile,
            jvmArgs = jvmArgs,
            disableIP = true,
        )

        gradleRunner.setupProject()
        gradleRunner.writeProjectFile("app/src/main/AndroidManifest.xml", manifestContent)
        gradleRunner.writeProjectFile("lib/src/main/AndroidManifest.xml", manifestContent)
        gradleRunner.runTasksAndCheckResult("detektMain", "reportMerge", "--continue") { result ->
            projectLayout.submodules.forEach { submodule ->
                assertThat(result.task(":${submodule.name}:detektMain")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
            }

            projectLayout.submodules.forEach {
                assertThat(projectFile("${it.name}/build/reports/detekt/debug.xml")).exists()
            }
            assertThat(projectFile("build/reports/detekt/merge.xml")).exists()
        }
    }
}
