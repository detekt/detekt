package io.gitlab.arturbosch.detekt.cli.runners

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.cli.BuildFailure
import io.gitlab.arturbosch.detekt.cli.CliArgs
import io.gitlab.arturbosch.detekt.cli.createSettings
import io.gitlab.arturbosch.detekt.cli.getOrComputeWeightedAmountOfIssues
import io.gitlab.arturbosch.detekt.cli.isValidAndSmallerOrEqual
import io.gitlab.arturbosch.detekt.cli.maxIssues
import io.gitlab.arturbosch.detekt.core.DetektFacade
import io.gitlab.arturbosch.detekt.core.NotApiButProbablyUsedByUsers
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import io.gitlab.arturbosch.detekt.core.measure
import io.gitlab.arturbosch.detekt.core.reporting.red
import java.io.PrintStream

@NotApiButProbablyUsedByUsers
class Runner(
    private val arguments: CliArgs,
    private val outputPrinter: PrintStream,
    private val errorPrinter: PrintStream
) : Executable {

    override fun execute() {
        arguments.createSettings(outputPrinter, errorPrinter)
            .use { settings ->
                val (serviceLoadingTime, facade) = measure { DetektFacade.create(settings) }
                settings.debug { "Loading services took $serviceLoadingTime ms" }
                val (engineRunTime, result) = measure { facade.run() }
                settings.debug { "Running core engine took $engineRunTime ms" }
                if (!arguments.createBaseline) {
                    checkBuildFailureThreshold(result, settings)
                }
            }
    }

    private fun checkBuildFailureThreshold(result: Detektion, settings: ProcessingSettings) {
        val amount = result.getOrComputeWeightedAmountOfIssues(settings.config)
        val maxIssues = settings.config.maxIssues()
        if (maxIssues.isValidAndSmallerOrEqual(amount)) {
            throw BuildFailure("Build failed with $amount weighted issues (threshold defined was $maxIssues).".red())
        }
    }
}
