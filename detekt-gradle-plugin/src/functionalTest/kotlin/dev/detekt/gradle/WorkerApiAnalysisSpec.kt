package dev.detekt.gradle

import dev.detekt.gradle.testkit.withResourceDir
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test

class WorkerApiAnalysisSpec {

    @Test
    fun `should not report false positive UnreachableCode when worker api is enabled`() {
        val result = GradleRunner.create()
            .withResourceDir("worker-api")
            .withPluginClasspath()
            .withArguments("detektMain", "--stacktrace")
            .build()

        assertThat(result.task(":detektMain")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
        assertThat(result.output)
            .doesNotContain("UnreachableCode")
            .doesNotContain("unreachable code")
    }

    @Test
    fun `should not report false positive RedundantSuspendModifier when worker api is enabled`() {
        val result = GradleRunner.create()
            .withResourceDir("worker-api")
            .withPluginClasspath()
            .withArguments("detektMain", "--stacktrace")
            .build()

        assertThat(result.task(":detektMain")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
        assertThat(result.output)
            .doesNotContain("RedundantSuspendModifier")
            .doesNotContain("redundant")
    }

    @Test
    fun `should correctly analyze with worker api disabled as baseline`() {
        val result = GradleRunner.create()
            .withResourceDir("worker-api")
            .withPluginClasspath()
            .withArguments("detektMain", "-Pdetekt.use.worker.api=false", "--stacktrace")
            .build()

        assertThat(result.task(":detektMain")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
        assertThat(result.output)
            .doesNotContain("UnreachableCode")
            .doesNotContain("RedundantSuspendModifier")
    }
}
