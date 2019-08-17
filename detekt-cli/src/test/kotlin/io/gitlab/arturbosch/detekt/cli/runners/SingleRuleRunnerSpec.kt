package io.gitlab.arturbosch.detekt.cli.runners

import io.gitlab.arturbosch.detekt.cli.CliArgs
import io.gitlab.arturbosch.detekt.test.resource
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Files
import java.nio.file.Paths

class SingleRuleRunnerSpec : Spek({

    val case = Paths.get(resource("cases/Poko.kt"))

    describe("single rule runner") {

        it("should load and run custom rule") {
            val tmp = Files.createTempFile("SingleRuleRunnerSpec", ".txt")
            val args = CliArgs.parse(arrayOf(
                "--input", case.toString(),
                "--report", "txt:$tmp",
                "--run-rule", "test:test"
            ))

            SingleRuleRunner(args).execute()

            assertThat(Files.readAllLines(tmp)).hasSize(1)
        }

        it("should throw on non existing rule") {
            val args = CliArgs.parse(arrayOf("--run-rule", "test:non_existing"))
            assertThatThrownBy { SingleRuleRunner(args).execute() }
                .isExactlyInstanceOf(IllegalArgumentException::class.java)
        }

        it("should throw on non existing rule set") {
            val args = CliArgs.parse(arrayOf("--run-rule", "non_existing:test"))
            assertThatThrownBy { SingleRuleRunner(args).execute() }
                .isExactlyInstanceOf(IllegalArgumentException::class.java)
        }
    }
})
