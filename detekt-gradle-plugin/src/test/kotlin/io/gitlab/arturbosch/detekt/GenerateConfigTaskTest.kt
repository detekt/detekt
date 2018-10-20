package io.gitlab.arturbosch.detekt

import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class GenerateConfigTaskTest : Spek({
	describe("The generate config task of the Detekt Gradle plugin") {
		listOf(DslTestBuilder.groovy(), DslTestBuilder.kotlin()).forEach { builder ->
			describe("using ${builder.gradleBuildName}") {
				it("can be executed without any configuration") {
					val gradleRunner = builder.build()

					gradleRunner.runTasksAndCheckResult("detektGenerateConfig") { result ->
						assertThat(result.task(":detektGenerateConfig")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
						assertThat(projectFile("default-detekt-config.yml")).exists()
					}
				}
			}
		}
	}
})