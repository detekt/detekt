package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.testkit.DslTestBuilder.Companion.groovy
import io.gitlab.arturbosch.detekt.testkit.DslTestBuilder.Companion.kotlin
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.spekframework.spek2.Spek
import org.spekframework.spek2.dsl.Skip
import org.spekframework.spek2.style.specification.describe

object GradleVersionTest : Spek({

    val gradleVersion = "5.4"

    describe(
        "detekt plugin running on oldest supported Gradle version",
        skip = if (getJdkVersion() < 13) Skip.No else Skip.Yes("Gradle $gradleVersion unsupported on this Java version")
    ) {
        listOf(groovy().dryRun(), kotlin().dryRun()).forEach { builder ->
            describe("using ${builder.gradleBuildName}") {
                it("runs on version $gradleVersion of Gradle") {
                    val gradleRunner = builder.withGradleVersion(gradleVersion).build()
                    gradleRunner.runDetektTaskAndCheckResult { result ->
                        assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                    }
                }
            }
        }
    }
})

internal fun getJdkVersion(): Int {
    val version = System.getProperty("java.version")
    val majorVersion = if (version.startsWith("1.")) {
        version.substring(2, 3)
    } else if (!version.contains('.') && version.contains('-')) {
        version.substringBefore('-') // early access Java.net versions
    } else {
        version.substringBefore('.')
    }
    return Integer.parseInt(majorVersion)
}
