package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.DslTestBuilder.Companion.groovy
import io.gitlab.arturbosch.detekt.DslTestBuilder.Companion.kotlin
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object MultiVersionTest : Spek({

    val testedGradleVersions = getGradleVersionsUnderTest()

    describe("detekt plugin running on different Gradle versions") {
        listOf(groovy().dryRun(), kotlin().dryRun()).forEach { builder ->
            describe("using ${builder.gradleBuildName}") {
                testedGradleVersions.forEach { gradleVersion ->
                    it("runs on version $gradleVersion of Gradle") {
                        val gradleRunner = builder.withGradleVersion(gradleVersion).build()
                        gradleRunner.runDetektTaskAndCheckResult { result ->
                            assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                        }
                    }
                }
            }
        }
    }
})

private fun getGradleVersionsUnderTest() =
    if (getJdkVersion() < 13) {
        listOf("5.4", "6.6")
    } else {
        listOf("6.6")
    }

private fun getJdkVersion(): Int {
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
