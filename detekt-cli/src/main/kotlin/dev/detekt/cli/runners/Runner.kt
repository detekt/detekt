package dev.detekt.cli.runners

import dev.detekt.cli.CliArgs
import dev.detekt.cli.createSpec
import dev.detekt.tooling.api.AnalysisResult
import dev.detekt.tooling.api.Detekt
import dev.detekt.tooling.api.DetektProvider
import dev.detekt.tooling.api.UnexpectedError
import dev.detekt.tooling.api.spec.ProcessingSpec
import dev.detekt.tooling.internal.NotApiButProbablyUsedByUsers
import java.util.concurrent.Callable

class Runner(private val spec: ProcessingSpec) :
    Executable,
    Callable<AnalysisResult> {

    @NotApiButProbablyUsedByUsers
    constructor(
        arguments: CliArgs,
        outputPrinter: Appendable,
        errorPrinter: Appendable,
    ) : this(arguments.createSpec(outputPrinter, errorPrinter))

    override fun execute() {
        val result = call()
        when (val error = result.error) {
            is UnexpectedError -> throw error.cause
            else -> error?.let { throw it }
        }
    }

    override fun call(): AnalysisResult {
        val provider = DetektProvider.load()
        val detekt: Detekt = provider.get(spec)
        return detekt.run()
    }
}
