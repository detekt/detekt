package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.DslTestBuilder.Companion.kotlin
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

/**
 * Tests that run the Detekt Gradle Plugins tasks multiple times to check for correct
 * UP-TO-DATE states and correct build caching.
 */
internal class PluginTaskBehaviorTest : Spek({

    val configFileName = "config.yml"
    val baselineFileName = "baseline.xml"

    val detektConfig = """
                    |detekt {
                    |    config = files("$configFileName")
                    |    baseline = file("$baselineFileName")
                    |}
                """

    describe("The Detekt Gradle Plugin :detekt Task") {
        lateinit var gradleRunner: DslGradleRunner

        beforeEachTest {
            gradleRunner = kotlin()
                .withDetektConfig(detektConfig)
                .withBaseline(baselineFileName)
                .withConfigFile(configFileName)
                .build()
        }

        it("should be UP-TO-DATE the 2nd run without changes") {
            gradleRunner.runDetektTaskAndCheckResult { result ->
                assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
            }
            gradleRunner.runDetektTaskAndCheckResult { result ->
                assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.UP_TO_DATE)
            }
        }
        it("should pick up build artifacts from the build cache on a 2nd run after deleting the build/ dir") {
            gradleRunner.runDetektTaskAndCheckResult { result ->
                assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
            }

            gradleRunner.projectFile("build").deleteRecursively()

            // Running detekt again should pick up artifacts from Build Cache
            gradleRunner.runDetektTaskAndCheckResult { result ->
                assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.FROM_CACHE)
            }
        }
        it("should pick up build artifacts from the build cache on a 2nd run after running :clean") {
            gradleRunner.runDetektTaskAndCheckResult { result ->
                assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
            }
            gradleRunner.runTasksAndCheckResult("clean", "detekt") { result ->
                assertThat(result.task(":clean")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.FROM_CACHE)
            }
        }
        it("should run again after changing config") {
            val configFileWithCommentsDisabled = """
                            |comments:
                            |  active: false
                        """.trimMargin()

            gradleRunner.runDetektTaskAndCheckResult { result ->
                assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
            }

            // update config file
            gradleRunner.writeProjectFile(configFileName, configFileWithCommentsDisabled)

            gradleRunner.runTasksAndCheckResult("detekt") { result ->
                assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
            }
        }
        it("should run again after changing baseline") {
            val changedBaselineContent = """
                            |<some>
                            |    <more/>
                            |    <xml/>
                            |</some>
                        """.trimMargin()

            gradleRunner.runDetektTaskAndCheckResult { result ->
                assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
            }

            // update baseline file
            gradleRunner.writeProjectFile(baselineFileName, changedBaselineContent)

            gradleRunner.runTasksAndCheckResult("detekt") { result ->
                assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
            }
        }
        it("should run again after changing inputs") {

            gradleRunner.runDetektTaskAndCheckResult { result ->
                assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
            }

            // add a new File
            gradleRunner.writeKtFile(gradleRunner.projectLayout.srcDirs.first(), "OtherKotlinClass")

            gradleRunner.runTasksAndCheckResult("detekt") { result ->
                assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
            }
        }
    }
})
