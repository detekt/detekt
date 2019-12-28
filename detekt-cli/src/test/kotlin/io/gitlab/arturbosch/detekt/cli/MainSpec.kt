package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.cli.runners.AstPrinter
import io.gitlab.arturbosch.detekt.cli.runners.ConfigExporter
import io.gitlab.arturbosch.detekt.cli.runners.Runner
import io.gitlab.arturbosch.detekt.cli.runners.SingleRuleRunner
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
                    emptyArray()
                ).forEach { args ->

                    val expectedRunnerClass = when {
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
})
