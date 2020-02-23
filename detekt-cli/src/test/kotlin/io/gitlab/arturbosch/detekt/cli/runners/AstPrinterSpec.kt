package io.gitlab.arturbosch.detekt.cli.runners

import io.gitlab.arturbosch.detekt.cli.CliArgs
import io.gitlab.arturbosch.detekt.test.resource
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.nio.file.Paths

class AstPrinterSpec : Spek({

    describe("element printer") {

        val path = Paths.get(resource("cases")).toString()

        describe("successful AST printing") {

            val outStream = System.out
            val output = ByteArrayOutputStream()

            beforeEachTest {
                System.setOut(PrintStream(output))
            }

            afterEachTest {
                System.setOut(outStream)
            }

            it("should print the AST as string") {
                val args = CliArgs()
                args.input = Paths.get(resource("cases/Poko.kt")).toString()
                val printer = AstPrinter(args)

                printer.execute()

                assertThat(output.toString()).isNotEmpty()
            }
        }

        it("throws an exception when declaring multiple input files") {
            val multiplePaths = "$path,$path"
            val args = CliArgs()
            args.input = multiplePaths
            val printer = AstPrinter(args)

            assertThatIllegalArgumentException()
                .isThrownBy { printer.execute() }
                .withMessage("More than one input path specified. Printing AST is only supported for single files.")
        }

        it("throws an exception when trying to print the AST of a directory") {
            val args = CliArgs()
            args.input = path
            val printer = AstPrinter(args)

            assertThatIllegalArgumentException()
                .isThrownBy { printer.execute() }
                .withMessageStartingWith("Input path ")
                .withMessageEndingWith(" must be a kotlin file and not a directory.")
        }
    }
})
