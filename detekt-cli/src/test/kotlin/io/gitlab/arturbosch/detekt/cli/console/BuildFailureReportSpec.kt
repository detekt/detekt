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

            it("should print a warning in yellow if threshold met") {
                subject.init(TestConfig(mapOf("warningThreshold" to "1")))
                val report = subject.render(detektion)
                val expectedMessage = "Warning: 1 weighted code smells found. Warning threshold is 1 and fail threshold is -1!"
                assertThat(report).isEqualTo(expectedMessage.yellow())
            }

            it("should throw a build failure error") {
                subject.init(TestConfig(mapOf("failThreshold" to "-2")))
                assertThatExceptionOfType(BuildFailure::class.java).isThrownBy { subject.render(detektion) }
            }

            it("should throw a build failure error when maxIssues met") {
                subject.init(TestConfig(mapOf("maxIssues" to "-2")))
                assertThatExceptionOfType(BuildFailure::class.java).isThrownBy { subject.render(detektion) }
            }

            it("should throw a build failure error even if warning threshold is also met") {
                subject.init(TestConfig(mapOf("failThreshold" to "-2", "warningThreshold" to "-2")))
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
