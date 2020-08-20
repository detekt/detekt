package io.gitlab.arturbosch.detekt.cli.runners

import io.github.detekt.parser.KtCompiler
import io.gitlab.arturbosch.detekt.cli.CliArgs
import java.nio.file.Files

class AstPrinter(
    private val arguments: CliArgs,
    private val outPrinter: Appendable
) : Executable {

    override fun execute() {
        val optionalInput = arguments.inputPaths.singleOrNull()
        val input = requireNotNull(optionalInput) {
            "More than one input path specified. Printing AST is only supported for single files."
        }

        require(Files.isRegularFile(input)) {
            "Input path $input must be a kotlin file and not a directory."
        }

        val ktFile = KtCompiler().compile(input, input)
        outPrinter.appendLine(ElementPrinter.dump(ktFile))
    }
}
