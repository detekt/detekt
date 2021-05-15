package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.invoke.CliArgument
import io.gitlab.arturbosch.detekt.testkit.DslGradleRunner
import io.gitlab.arturbosch.detekt.testkit.ProjectLayout
import org.assertj.core.api.Assertions.assertThat
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.repositories
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object DetektJvmSpec : Spek({
    describe("When applying detekt in a JVM project") {
        context("disabled TXT report") {

            val gradleRunner = DslGradleRunner(
                projectLayout = ProjectLayout(numberOfSourceFilesInRootPerSourceDir = 1),
                buildFileName = "build.gradle",
                baselineFiles = listOf("detekt-baseline.xml", "detekt-baseline-main.xml", "detekt-baseline-test.xml"),
                projectScript = {
                    apply<KotlinPluginWrapper>()
                    apply<DetektPlugin>()
                    repositories {
                        mavenCentral()
                        mavenLocal()
                    }
                    tasks.withType(Detekt::class.java).configureEach {
                        it.reports { reports ->
                            reports.txt.required.set(false)
                        }
                    }
                },
            )
            gradleRunner.setupProject()

            it("configures detekt type resolution task main") {
                val project = gradleRunner.buildProject()

                project.getTasksByName("detektMain", true) // trigger project evaluation

                val detektTask = project.tasks.getByPath("detektMain") as Detekt
                val argumentString = detektTask.arguments.flatMap(CliArgument::toArgument).joinToString(" ")

                assertThat(argumentString).containsPattern("""--baseline \S*[/\\]detekt-baseline-main.xml """)
                assertThat(argumentString).contains("--report xml:")
                assertThat(argumentString).contains("--report sarif:")
                assertThat(argumentString).doesNotContain("--report txt:")
                assertThat(argumentString).contains("--classpath")
            }

            it("configures detekt type resolution task test") {
                val project = gradleRunner.buildProject()

                project.getTasksByName("detektTest", true) // trigger project evaluation

                val detektTask = project.tasks.getByPath("detektTest") as Detekt
                val argumentString = detektTask.arguments.flatMap(CliArgument::toArgument).joinToString(" ")

                assertThat(argumentString).containsPattern("""--baseline \S*[/\\]detekt-baseline-test.xml """)
                assertThat(argumentString).contains("--report xml:")
                assertThat(argumentString).contains("--report sarif:")
                assertThat(argumentString).doesNotContain("--report txt:")
                assertThat(argumentString).contains("--classpath")
            }
        }

        context("report location set on extension & task") {
            val gradleRunner = DslGradleRunner(
                projectLayout = ProjectLayout(numberOfSourceFilesInRootPerSourceDir = 1),
                buildFileName = "build.gradle",
                mainBuildFileContent = """
                    plugins {
                        id "org.jetbrains.kotlin.jvm"
                        id "io.gitlab.arturbosch.detekt"
                    }

                    repositories {
                        mavenCentral()
                        mavenLocal()
                    }

                    detekt {
                        reports {
                            txt.destination = file("output-path.txt")
                        }
                    }

                    tasks.withType(io.gitlab.arturbosch.detekt.Detekt).configureEach {
                        reports {
                            txt.destination = file("output-path2.txt")
                        }
                    }
                """.trimIndent(),
                dryRun = false
            )
            gradleRunner.setupProject()

            it("logs a warning") {
                gradleRunner.runTasksAndCheckResult(":detektMain") { buildResult ->
                    assertThat(buildResult.output).contains("TXT report location set on detekt {} extension will be ignored for detektMain task.")
                }
            }
        }

        context("report location set on task only") {
            val gradleRunner = DslGradleRunner(
                projectLayout = ProjectLayout(numberOfSourceFilesInRootPerSourceDir = 1),
                buildFileName = "build.gradle",
                mainBuildFileContent = """
                    plugins {
                        id "org.jetbrains.kotlin.jvm"
                        id "io.gitlab.arturbosch.detekt"
                    }

                    repositories {
                        mavenCentral()
                        mavenLocal()
                    }

                    tasks.withType(io.gitlab.arturbosch.detekt.Detekt).configureEach {
                        reports {
                            txt.destination = file("output-path2.txt")
                        }
                    }
                """.trimIndent(),
                dryRun = false
            )
            gradleRunner.setupProject()

            it("logs a warning") {
                gradleRunner.runTasksAndCheckResult(":detektMain") { buildResult ->
                    assertThat(buildResult.output).doesNotContain("report location set on detekt {} extension will be ignored")
                }
            }
        }
    }
})
