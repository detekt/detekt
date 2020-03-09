package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.cli.runners.AstPrinter
import io.gitlab.arturbosch.detekt.cli.runners.ConfigExporter
import io.gitlab.arturbosch.detekt.cli.runners.Runner
import io.gitlab.arturbosch.detekt.cli.runners.SingleRuleRunner
import io.gitlab.arturbosch.detekt.cli.runners.VersionPrinter
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class MainSpec : Spek({

    describe("build runner") {

        listOf(PrintStream(ByteArrayOutputStream()), null).forEach { printer ->

            context("printer is [${if (printer == null) "default" else "provided"}]") {

                listOf(
                    arrayOf("--generate-config"),
                    arrayOf("--run-rule", "Rule"),
                    arrayOf("--print-ast"),
                    arrayOf("--version"),
                    emptyArray()
                ).forEach { args ->

                    val expectedRunnerClass = when {
                        args.contains("--version") -> VersionPrinter::class
                        args.contains("--generate-config") -> ConfigExporter::class
                        args.contains("--run-rule") -> SingleRuleRunner::class
                        args.contains("--print-ast") -> AstPrinter::class
                        else -> Runner::class
                    }

                    it("returns [${expectedRunnerClass.simpleName}] when arguments are $args") {
                        val runner = if (printer == null) buildRunner(args) else buildRunner(args, printer, printer)

                        assertThat(runner).isExactlyInstanceOf(expectedRunnerClass.java)
                    }
                }
            }
        }
    }

    describe("check arguments") {

        it("fails with --create-baseline but without --baseline") {
            val out = ByteArrayOutputStream()
            val err = ByteArrayOutputStream()

            try {
                val args = arrayOf("--create-baseline")

                buildRunner(args, PrintStream(out), PrintStream(err))
                Assertions.fail("This should throw an exception.")
            } catch (_: HandledArgumentViolation) {
                assertThat(String(err.toByteArray()).trim())
                    .isEqualTo("Creating a baseline.xml requires the --baseline parameter to specify a path.")
            }
        }

        it("succeed --create-baseline and --baseline") {
            val out = ByteArrayOutputStream()
            val err = ByteArrayOutputStream()

            val args = arrayOf(
                "--create-baseline",
                "--baseline",
                "baseline.xml"
            )

            buildRunner(args, PrintStream(out), PrintStream(err))

            assertThat(String(err.toByteArray())).isEmpty()
        }
    }
})
