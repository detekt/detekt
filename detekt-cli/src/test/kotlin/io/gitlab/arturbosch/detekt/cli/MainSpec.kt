package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.cli.runners.AstPrinter
import io.gitlab.arturbosch.detekt.cli.runners.ConfigExporter
import io.gitlab.arturbosch.detekt.cli.runners.Runner
import io.gitlab.arturbosch.detekt.cli.runners.SingleRuleRunner
import io.gitlab.arturbosch.detekt.cli.runners.VersionPrinter
import io.gitlab.arturbosch.detekt.test.NullPrintStream
import io.gitlab.arturbosch.detekt.test.StringPrintStream
import io.gitlab.arturbosch.detekt.test.resource
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Paths

class MainSpec : Spek({

    describe("build runner") {

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
                val runner = buildRunner(args, NullPrintStream(), NullPrintStream())

                assertThat(runner).isExactlyInstanceOf(expectedRunnerClass.java)
            }
        }
    }

    describe("check arguments") {

        it("fails with --create-baseline but without --baseline") {
            val out = StringPrintStream()
            val err = StringPrintStream()

            try {
                val args = arrayOf("--create-baseline")

                buildRunner(args, out, err)
                Assertions.fail("This should throw an exception.")
            } catch (_: HandledArgumentViolation) {
                assertThat(err.toString())
                    .isEqualTo("Creating a baseline.xml requires the --baseline parameter to specify a path.$LN$LN")
            }
        }

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

        it("fails with --baseline if the file does not exist") {
            val out = StringPrintStream()
            val err = StringPrintStream()

            val path = Paths.get("doesNotExist.xml")
            try {
                val args = arrayOf("--baseline", path.toString())

                buildRunner(args, out, err)
                Assertions.fail("This should throw an exception.")
            } catch (_: HandledArgumentViolation) {
                assertThat(err.toString())
                    .isEqualTo("The file specified by --baseline should exist '$path'.$LN$LN")
            }
        }

        it("fails with --baseline if the path is a directory") {
            val out = StringPrintStream()
            val err = StringPrintStream()

            val path = Paths.get(resource("/"))
            try {
                val args = arrayOf("--baseline", path.toString())

                buildRunner(args, out, err)
                Assertions.fail("This should throw an exception.")
            } catch (_: HandledArgumentViolation) {
                assertThat(err.toString())
                    .isEqualTo("The path specified by --baseline should be a file '$path'.$LN$LN")
            }
        }

        it("succeeds with --baseline if the path exists and is a file") {
            val out = StringPrintStream()
            val err = StringPrintStream()

            val path = Paths.get(resource("smell-baseline.xml"))

            val args = arrayOf("--baseline", path.toString())

            buildRunner(args, out, err)

            assertThat(err.toString()).isEmpty()
        }
    }
})

private val LN = System.lineSeparator()
