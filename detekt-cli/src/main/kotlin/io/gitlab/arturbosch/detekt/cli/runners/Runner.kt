package io.gitlab.arturbosch.detekt.cli.runners

import io.github.detekt.tooling.api.Detekt
import io.github.detekt.tooling.api.DetektProvider
import io.github.detekt.tooling.api.UnexpectedError
import io.github.detekt.tooling.api.spec.ProcessingSpec
import io.gitlab.arturbosch.detekt.cli.CliArgs
import io.gitlab.arturbosch.detekt.cli.createSpec
import io.gitlab.arturbosch.detekt.core.NotApiButProbablyUsedByUsers
import java.io.PrintStream

@NotApiButProbablyUsedByUsers
class Runner(private val spec: ProcessingSpec) : Executable {

    constructor(
        arguments: CliArgs,
        outputPrinter: PrintStream,
        errorPrinter: PrintStream,
    ) : this(arguments.createSpec(outputPrinter, errorPrinter))

    override fun execute() {
        val provider = DetektProvider.load()
        val detekt: Detekt = provider.get(spec)
        val result = detekt.run()
        when (val error = result.error) {
            is UnexpectedError -> throw error.cause
            else -> error?.let { throw it }
        }
    }
}
