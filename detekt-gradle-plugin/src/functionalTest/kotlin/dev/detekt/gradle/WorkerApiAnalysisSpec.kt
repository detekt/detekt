package dev.detekt.gradle

import dev.detekt.gradle.testkit.withResourceDir
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class WorkerApiAnalysisSpec {

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `should not report false positives with Analysis API rules`(workerApiEnabled: Boolean) {
        val args = buildList {
            add("detektMain")
            add("--stacktrace")
            if (!workerApiEnabled) {
                add("-Pdetekt.use.worker.api=false")
            }
        }

        val result = GradleRunner.create()
            .withResourceDir("worker-api")
            .withPluginClasspath()
            .withArguments(*args.toTypedArray())
            .build()

        assertThat(result.task(":detektMain")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
        assertThat(result.output)
            .doesNotContain("UnreachableCode")
            .doesNotContain("unreachable code")
            .doesNotContain("RedundantSuspendModifier")
            .doesNotContain("redundant")
    }
}
