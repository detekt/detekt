package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.testkit.DslTestBuilder
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.condition.EnabledForJreRange
import org.junit.jupiter.api.condition.JRE.JAVA_13
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class GradleVersionSpec {

    @ParameterizedTest(name = "Using {0}, runs on version $gradleVersion")
    @MethodSource("io.gitlab.arturbosch.detekt.testkit.DslTestBuilder#builders")
    @EnabledForJreRange(max = JAVA_13, disabledReason = "Gradle $gradleVersion unsupported on this Java version")
    fun runsOnOldestSupportedGradleVersion(builder: DslTestBuilder) {
        val gradleRunner = builder.dryRun().withGradleVersion(gradleVersion).build()
        gradleRunner.runDetektTaskAndCheckResult { result ->
            assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
        }
    }

    companion object {
        const val gradleVersion = "6.1"
    }
}
