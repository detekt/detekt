package io.gitlab.arturbosch.detekt.sample.extensions

import io.github.detekt.test.utils.NullPrintStream
import io.github.detekt.test.utils.createTempDirectoryForTest
import io.gitlab.arturbosch.detekt.cli.CliArgs
import io.gitlab.arturbosch.detekt.core.config.InvalidConfig
import io.gitlab.arturbosch.detekt.cli.runners.Runner
import org.assertj.core.api.Assertions.assertThatCode
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class SupportConfigValidationSpec : Spek({

    describe("support config validation") {

        val testDir = createTempDirectoryForTest("detekt-sample")

        context("failing cases") {
            arrayOf(
                "fails when new rule set is not excluded" to "included-config.yml",
                "fails due to no configuration property present for 'sample' rule set" to "wrong-property-config.yml"
            ).forEach { (testCase, config) ->
                it(testCase) {
                    val args = CliArgs {
                        input = testDir.toString()
                        configResource = config
                    }

                    assertThatCode { Runner(args, NullPrintStream(), NullPrintStream()).execute() }
                        .isInstanceOf(InvalidConfig::class.java)
                        .hasMessageContaining("Run failed with 1 invalid config property.")
                }
            }
        }

        it("passes with excluded new rule set") {
            val args = CliArgs {
                input = testDir.toString()
                configResource = "excluded-config.yml"
            }

            assertThatCode { Runner(args, NullPrintStream(), NullPrintStream()).execute() }
                .doesNotThrowAnyException()
        }
    }
})
