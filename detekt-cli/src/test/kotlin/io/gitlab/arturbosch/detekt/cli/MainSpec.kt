@file:Suppress("DEPRECATION")

package io.gitlab.arturbosch.detekt.cli

import io.github.detekt.test.utils.NullPrintStream
import io.github.detekt.test.utils.StringPrintStream
import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.cli.runners.AstPrinter
import io.gitlab.arturbosch.detekt.cli.runners.ConfigExporter
import io.gitlab.arturbosch.detekt.cli.runners.Runner
import io.gitlab.arturbosch.detekt.cli.runners.VersionPrinter
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class MainSpec : Spek({

    describe("build runner") {

        listOf(
            arrayOf("--generate-config"),
            arrayOf("--run-rule", "RuleSet:Rule"),
            arrayOf("--print-ast"),
            arrayOf("--version"),
            emptyArray()
        ).forEach { args ->

            val expectedRunnerClass = when {
                args.contains("--version") -> VersionPrinter::class
                args.contains("--generate-config") -> ConfigExporter::class
                args.contains("--run-rule") -> Runner::class
                args.contains("--print-ast") -> AstPrinter::class
                else -> Runner::class
            }

            it("returns [${expectedRunnerClass.simpleName}] when arguments are $args") {
                val runner = buildRunner(args, NullPrintStream(), NullPrintStream())

                assertThat(runner).isExactlyInstanceOf(expectedRunnerClass.java)
            }
        }
    }

    describe("Runner creates baselines") {

        it("succeeds with --create-baseline and --baseline") {
            val out = StringPrintStream()
            val err = StringPrintStream()

            val args = arrayOf(
                "--create-baseline",
                "--baseline",
                "baseline.xml"
            )

            buildRunner(args, out, err)

            assertThat(err.toString()).isEmpty()
        }

        it("succeeds with --baseline if the path exists and is a file") {
            val out = StringPrintStream()
            val err = StringPrintStream()

            val path = resourceAsPath("/configs/baseline-empty.xml")

            val args = arrayOf("--baseline", path.toString())

            buildRunner(args, out, err)

            assertThat(err.toString()).isEmpty()
        }
    }
})
