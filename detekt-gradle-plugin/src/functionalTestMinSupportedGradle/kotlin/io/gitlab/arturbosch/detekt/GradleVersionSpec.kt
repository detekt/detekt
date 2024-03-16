package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.testkit.DslTestBuilder
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.gradle.testkit.runner.internal.PluginUnderTestMetadataReading
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledForJreRange
import org.junit.jupiter.api.condition.JRE.JAVA_15
import java.nio.file.Paths

class GradleVersionSpec {

    @Test
    @DisplayName("Runs on version $GRADLE_VERSION")
    @EnabledForJreRange(max = JAVA_15, disabledReason = "Gradle $GRADLE_VERSION unsupported on this Java version")
    fun runsOnOldestSupportedGradleVersion() {
        val builder = DslTestBuilder.kotlin()
        val metadataUrl =
            Paths.get("build/gradleMinVersionPluginUnderTestMetadata/plugin-under-test-metadata.properties")
                .toUri()
                .toURL()
        val gradleRunner = builder
            .withGradleVersion(GRADLE_VERSION)
            .withPluginClasspath(PluginUnderTestMetadataReading.readImplementationClasspath(metadataUrl))
            .build()
        gradleRunner.runDetektTaskAndCheckResult { result ->
            assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
        }
    }

    @Test
    @DisplayName("Runs on version $GRADLE_VERSION with worker API enabled")
    @EnabledForJreRange(max = JAVA_15, disabledReason = "Gradle $GRADLE_VERSION unsupported on this Java version")
    @Disabled("https://github.com/detekt/detekt/issues/7058")
    fun runsOnOldestSupportedGradleVersionWithWorkerApi() {
        val builder = DslTestBuilder.kotlin()
        val metadataUrl =
            Paths.get("build/gradleMinVersionPluginUnderTestMetadata/plugin-under-test-metadata.properties")
                .toUri()
                .toURL()
        val gradleRunner = builder
            .withGradleVersion(GRADLE_VERSION)
            .withPluginClasspath(PluginUnderTestMetadataReading.readImplementationClasspath(metadataUrl))
            .build()
        gradleRunner.runTasksAndCheckResult(
            "detekt",
            "-Pdetekt.use.worker.api=true",
        ) { result ->
            assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
        }
    }

    companion object {
        const val GRADLE_VERSION = "6.8.3"
    }
}
