package io.gitlab.arturbosch.detekt.sample.extensions

import io.gitlab.arturbosch.detekt.cli.CliArgs
import io.gitlab.arturbosch.detekt.cli.InvalidConfig
import io.gitlab.arturbosch.detekt.cli.runners.Runner
import org.assertj.core.api.Assertions.assertThatCode
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Files

class SupportConfigValidationSpec : Spek({

    describe("support config validation") {

        val testDir = Files.createTempDirectory("detekt-sample")

        it("fails when new rule set is not excluded") {
            val args = CliArgs {
                input = testDir.toString()
                configResource = "not-excluded-config.yml"
            }

            assertThatCode { Runner(args).execute() }
                .isInstanceOf(InvalidConfig::class.java)
                .hasMessage("Run failed with 1 invalid config property.")
        }

        it("passes with excluded new rule set") {
            val args = CliArgs {
                input = testDir.toString()
                configResource = "excluded-config.yml"
            }

            assertThatCode { Runner(args).execute() }
                .doesNotThrowAnyException()
        }
    }
})
