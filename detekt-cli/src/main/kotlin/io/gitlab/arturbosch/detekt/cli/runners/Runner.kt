package io.gitlab.arturbosch.detekt.cli.runners

import io.github.detekt.tooling.api.Detekt
import io.github.detekt.tooling.api.DetektProvider
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

        result.error?.let { throw it }
//            .use { settings ->
//                val (serviceLoadingTime, facade) = measure { DetektFacade.create(settings) }
//                settings.debug { "Loading services took $serviceLoadingTime ms" }
//                val (engineRunTime, result) = measure { facade.run() }
//                settings.debug { "Running core engine took $engineRunTime ms" }
//                if (!arguments.createBaseline) {
//                    checkBuildFailureThreshold(result, settings)
//                }
//            }
    }

//    private fun checkBuildFailureThreshold(result: Detektion, settings: ProcessingSettings) {
//        val amount = result.getOrComputeWeightedAmountOfIssues(settings.config)
//        val maxIssues = settings.config.maxIssues()
//        if (maxIssues.isValidAndSmallerOrEqual(amount)) {
//            throw BuildFailure("Build failed with $amount weighted issues (threshold defined was $maxIssues).".red())
//        }
//    }
}
