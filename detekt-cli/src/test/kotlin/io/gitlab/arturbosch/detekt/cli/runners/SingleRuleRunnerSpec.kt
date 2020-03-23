package io.gitlab.arturbosch.detekt.cli.runners

import io.gitlab.arturbosch.detekt.cli.createCliArgs
import io.gitlab.arturbosch.detekt.test.NullPrintStream
import io.gitlab.arturbosch.detekt.test.resource
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.lang.IllegalStateException
import java.nio.file.Files
import java.nio.file.Paths

class SingleRuleRunnerSpec : Spek({

    val case = Paths.get(resource("cases/Poko.kt"))

    describe("single rule runner") {

        it("should load and run custom rule") {
            val tmp = Files.createTempFile("SingleRuleRunnerSpec", ".txt")
            val args = createCliArgs(
                "--input", case.toString(),
                "--report", "txt:$tmp",
                "--run-rule", "test:test"
            )

            SingleRuleRunner(args, NullPrintStream(), NullPrintStream()).execute()

            assertThat(Files.readAllLines(tmp)).hasSize(1)
        }

        it("should throw on non existing rule") {
            val args = createCliArgs("--run-rule", "test:non_existing")
            assertThatThrownBy { SingleRuleRunner(args, NullPrintStream(), NullPrintStream()).execute() }
                .isExactlyInstanceOf(IllegalArgumentException::class.java)
        }

        it("should throw on non existing rule set") {
            val args = createCliArgs("--run-rule", "non_existing:test")
            assertThatThrownBy { SingleRuleRunner(args, NullPrintStream(), NullPrintStream()).execute() }
                .isExactlyInstanceOf(IllegalArgumentException::class.java)
        }

        it("should throw on non existing run-rule") {
            val args = createCliArgs()
            assertThatThrownBy { SingleRuleRunner(args, NullPrintStream(), NullPrintStream()).execute() }
                .isExactlyInstanceOf(IllegalStateException::class.java)
                .withFailMessage("Unexpected empty 'runRule' argument.")
        }
    }
})
