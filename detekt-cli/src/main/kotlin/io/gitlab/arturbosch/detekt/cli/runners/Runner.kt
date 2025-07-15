package io.gitlab.arturbosch.detekt.cli.runners

import io.github.detekt.tooling.api.AnalysisResult
import io.github.detekt.tooling.api.Detekt
import io.github.detekt.tooling.api.DetektProvider
import io.github.detekt.tooling.api.UnexpectedError
import io.github.detekt.tooling.api.spec.ProcessingSpec
import io.github.detekt.tooling.internal.NotApiButProbablyUsedByUsers
import io.gitlab.arturbosch.detekt.cli.CliArgs
import io.gitlab.arturbosch.detekt.cli.createSpec
import java.util.concurrent.Callable

class Runner(private val spec: ProcessingSpec) : Executable, Callable<AnalysisResult> {

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
