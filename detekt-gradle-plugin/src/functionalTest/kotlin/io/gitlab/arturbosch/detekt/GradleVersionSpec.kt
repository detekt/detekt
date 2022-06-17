package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.testkit.DslTestBuilder
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledForJreRange
import org.junit.jupiter.api.condition.JRE.JAVA_15

class GradleVersionSpec {

    @Test
    @DisplayName("Runs on version $gradleVersion")
    @EnabledForJreRange(max = JAVA_15, disabledReason = "Gradle $gradleVersion unsupported on this Java version")
    fun runsOnOldestSupportedGradleVersion() {
        val builder = DslTestBuilder.kotlin()
        val gradleRunner = builder.withGradleVersion(gradleVersion).build()
        gradleRunner.runDetektTaskAndCheckResult { result ->
            assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
        }
    }

    companion object {
        const val gradleVersion = "6.7.1"
    }
}
