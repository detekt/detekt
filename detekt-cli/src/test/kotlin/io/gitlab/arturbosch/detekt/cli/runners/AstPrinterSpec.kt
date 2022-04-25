package io.gitlab.arturbosch.detekt.cli.runners

import io.github.detekt.test.utils.NullPrintStream
import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.cli.CliArgs
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class AstPrinterSpec {

    val path = resourceAsPath("cases").toString()

    @Nested
    inner class `successful AST printing` {

        @Test
        fun `should print the AST as string`() {
            val output = ByteArrayOutputStream()
            val args = CliArgs()
            args.input = resourceAsPath("cases/Poko.kt").toString()
            val printer = AstPrinter(args, PrintStream(output))

            printer.execute()

            assertThat(output.toString()).isNotEmpty()
        }
    }

    @Test
    fun `throws an exception when declaring multiple input files`() {
        val multiplePaths = "$path,$path"
        val args = CliArgs()
        args.input = multiplePaths
        val printer = AstPrinter(args, NullPrintStream())

        assertThatIllegalArgumentException()
            .isThrownBy { printer.execute() }
            .withMessage("More than one input path specified. Printing AST is only supported for single files.")
    }

    @Test
    fun `throws an exception when trying to print the AST of a directory`() {
        val args = CliArgs()
        args.input = path
        val printer = AstPrinter(args, NullPrintStream())

        assertThatIllegalArgumentException()
            .isThrownBy { printer.execute() }
            .withMessageStartingWith("Input path ")
            .withMessageEndingWith(" must be a kotlin file and not a directory.")
    }
}
