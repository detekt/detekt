package io.gitlab.arturbosch.detekt.cli.runners

import io.gitlab.arturbosch.detekt.cli.CliArgs
import io.github.detekt.parser.KtCompiler
import io.gitlab.arturbosch.detekt.core.isFile
import java.io.PrintStream

class AstPrinter(
    private val arguments: CliArgs,
    private val outPrinter: PrintStream
) : Executable {

    override fun execute() {
        val optionalInput = arguments.inputPaths.singleOrNull()
        val input = requireNotNull(optionalInput) {
            "More than one input path specified. Printing AST is only supported for single files."
        }

        require(input.isFile()) {
            "Input path $input must be a kotlin file and not a directory."
        }

        val ktFile = KtCompiler().compile(input, input)
        outPrinter.println(ElementPrinter.dump(ktFile))
    }
}
