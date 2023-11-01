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
    @DisplayName("Runs on version $GRADLE_VERSION")
    @EnabledForJreRange(max = JAVA_15, disabledReason = "Gradle $GRADLE_VERSION unsupported on this Java version")
    fun runsOnOldestSupportedGradleVersion() {
        val builder = DslTestBuilder.kotlin()
        val gradleRunner = builder.withGradleVersion(GRADLE_VERSION).build()
        gradleRunner.runDetektTaskAndCheckResult { result ->
            assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
        }
    }

    companion object {
        const val GRADLE_VERSION = "6.8.3"
    }
}
