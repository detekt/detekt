package io.gitlab.arturbosch.detekt.cli.runners

import io.gitlab.arturbosch.detekt.cli.CliArgs
import io.gitlab.arturbosch.detekt.core.KtCompiler
import io.gitlab.arturbosch.detekt.core.isFile

class AstPrinter(private val arguments: CliArgs) : Executable {

    override fun execute() {
        val input = arguments.inputPaths.singleOrNull()
        requireNotNull(input) {
            "More than one input path specified. Printing AST is only supported for single files."
        }
        require(input.isFile()) {
            "Input path must be a kotlin file and not a directory."
        }

        val ktFile = KtCompiler().compile(input, input)
        println(ElementPrinter.dump(ktFile))
    }
}
