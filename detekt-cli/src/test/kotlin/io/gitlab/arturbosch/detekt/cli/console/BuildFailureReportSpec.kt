package io.gitlab.arturbosch.detekt.cli.console

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.cli.TestDetektion
import io.gitlab.arturbosch.detekt.cli.createFinding
import io.gitlab.arturbosch.detekt.test.TestConfig
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.assertj.core.api.Assertions.assertThatIllegalStateException
import org.assertj.core.api.Assertions.fail
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

/**
 * @author Artur Bosch
 */
internal class BuildFailureReportSpec : Spek({

    val subject by memoized { BuildFailureReport() }

    describe("build failure threshold is configurable by configuration") {

        describe("empty code smell result") {
            val detektion = TestDetektion(createFinding())

            it("should fail because no config is provided for configurable console reporter") {
                assertThatIllegalStateException().isThrownBy { subject.render(detektion) }
            }

            it("should return no report if build failure not configured") {
                subject.init(Config.empty)
                val report = subject.render(detektion)
                assertThat(report).isNull()
            }

            it("should throw a build failure error when maxIssues met") {
                subject.init(TestConfig(mapOf("maxIssues" to "-2")))
                assertThatExceptionOfType(BuildFailure::class.java).isThrownBy { subject.render(detektion) }
            }

            it("should contain no stacktrace on fail") {
                subject.init(TestConfig(mapOf("maxIssues" to "-2")))
                try {
                    subject.render(detektion)
                    fail("Render should throw")
                } catch (e: BuildFailure) {
                    assertThat(e.stackTrace).isEmpty()
                }
            }

            it("should print a warning in yellow if weighted issues are not zero but below threshold") {
                subject.init(TestConfig(mapOf("maxIssues" to "10")))
                val report = subject.render(detektion)
                val expectedMessage = "Build succeeded with 1 weighted issues (threshold defined was 10)."
                assertThat(report).isEqualTo(expectedMessage.yellow())
            }

            it("should not print a warning if weighted issues are zero") {
                subject.init(TestConfig(mapOf("maxIssues" to "10")))
                val report = subject.render(TestDetektion())
                assertThat(report).isNull()
            }
        }
    }

    describe("reached extension function tests") {
        subject.apply {
            assertThat(0.reached(0)).isEqualTo(false)

            assertThat((-1).reached(0)).isEqualTo(false)

            assertThat(1.reached(0)).isEqualTo(false)
            assertThat(1.reached(1)).isEqualTo(true)
            assertThat(1.reached(2)).isEqualTo(true)

            assertThat(12.reached(11)).isEqualTo(false)
            assertThat(12.reached(12)).isEqualTo(true)
            assertThat(12.reached(13)).isEqualTo(true)
        }
    }
})
