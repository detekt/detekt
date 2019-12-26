package io.gitlab.arturbosch.detekt.cli.console

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.cli.createFinding
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.TestDetektion
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalStateException
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

internal class BuildFailureReportSpec : Spek({

    val subject by memoized { BuildFailureReport() }

    describe("build failure threshold is configurable by configuration") {

        describe("empty code smell result") {
            val codeSmell = createFinding()
            val detektion = TestDetektion(codeSmell)

            it("should fail because no config is provided for configurable console reporter") {
                assertThatIllegalStateException().isThrownBy { subject.render(detektion) }
            }

            it("should return no report if build failure not configured") {
                subject.init(Config.empty)
                val report = subject.render(detektion)
                assertThat(report).isNull()
            }

            it("should print an error in red ") {
                subject.init(TestConfig(mapOf("maxIssues" to "-2")))
                val expectedMessage = "Build failed with 1 weighted issues (threshold defined was -2)."
                assertThat(subject.render(detektion)).isEqualTo(expectedMessage.red())
            }

            it("should print a warning in yellow if weighted issues are not zero but below threshold") {
                subject.init(TestConfig(mapOf("maxIssues" to "10")))
                val expectedMessage = "Build succeeded with 1 weighted issues (threshold defined was 10)."
                assertThat(subject.render(detektion)).isEqualTo(expectedMessage.yellow())
            }

            it("should not print a warning if weighted issues are zero") {
                subject.init(TestConfig(mapOf("maxIssues" to "0")))
                val report = subject.render(TestDetektion())
                assertThat(report).isNull()
            }

            it("should not report auto corrected issues as build failure") {
                AutoCorrectableIssueAssert.isReportNull(subject)
            }
        }
    }
})
