package io.gitlab.arturbosch.detekt.cli.runners

import io.github.detekt.test.utils.createTempFileForTest
import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.cli.createCliArgs
import io.gitlab.arturbosch.detekt.cli.createRunner
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Files

class SingleRuleRunnerSpec : Spek({

    val case = resourceAsPath("cases/Poko.kt")

    describe("single rule runner") {

        it("should load and run custom rule") {
            val tmp = createTempFileForTest("SingleRuleRunnerSpec", ".txt")
            val args = createCliArgs(
                "--input", case.toString(),
                "--report", "txt:$tmp",
                "--run-rule", "test:test"
            )

            runCatching { createRunner(args).execute() }

            assertThat(Files.readAllLines(tmp)).hasSize(1)
        }

        it("should throw on non existing rule") {
            val args = createCliArgs("--run-rule", "test:non_existing")
            assertThatThrownBy { createRunner(args).execute() }
                .isExactlyInstanceOf(IllegalArgumentException::class.java)
        }

        it("should throw on non existing rule set") {
            val args = createCliArgs("--run-rule", "non_existing:test")
            assertThatThrownBy { createRunner(args).execute() }
                .isExactlyInstanceOf(IllegalArgumentException::class.java)
        }

        it("should throw on non existing run-rule") {
            val args = createCliArgs("--run-rule", "")
            assertThatThrownBy { createRunner(args).execute() }
                .isExactlyInstanceOf(IllegalArgumentException::class.java)
                .withFailMessage("Unexpected empty 'runRule' argument.")
        }
    }
})
