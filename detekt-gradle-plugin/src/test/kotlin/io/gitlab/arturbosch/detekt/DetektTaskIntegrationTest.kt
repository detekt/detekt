package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.DslTestBuilder.Companion.groovy
import io.gitlab.arturbosch.detekt.DslTestBuilder.Companion.kotlin
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

internal class DetektTaskIntegrationTest : Spek({

    describe("When applying the detekt gradle plugin") {
        listOf(groovy(), kotlin()).forEach { builder ->
            context(builder.gradleBuildName) {
                describe("using the ignoreFailures toggle") {
                    val projectLayoutWithTooManyIssues = ProjectLayout(
                        numberOfSourceFilesInRootPerSourceDir = 15,
                        numberOfCodeSmellsInRootPerSourceDir = 15
                    )

                    it("build succeeds with more issues than threshold if enabled") {

                        val config = """
                            |detekt {
                            |   ignoreFailures = true
                            |}
                            """

                        val gradleRunner = builder
                            .withProjectLayout(projectLayoutWithTooManyIssues)
                            .withDetektConfig(config)
                            .build()

                        gradleRunner.runDetektTaskAndCheckResult { result ->
                            assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                        }
                    }
                    it("build fails with more issues than threshold successfully if disabled") {

                        val config = """
                            |detekt {
                            |   ignoreFailures = false
                            |}
                            """

                        val gradleRunner = builder
                            .withProjectLayout(projectLayoutWithTooManyIssues)
                            .withDetektConfig(config)
                            .build()

                        gradleRunner.runDetektTaskAndExpectFailure()
                    }
                }
            }
        }
    }
})
