package io.gitlab.arturbosch.detekt.cli.runners

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.cli.BuildFailure
import io.gitlab.arturbosch.detekt.cli.CliArgs
import io.gitlab.arturbosch.detekt.cli.FilteredDetectionResult
import io.gitlab.arturbosch.detekt.cli.OutputFacade
import io.gitlab.arturbosch.detekt.cli.baseline.BaselineFacade
import io.gitlab.arturbosch.detekt.cli.config.checkConfiguration
import io.gitlab.arturbosch.detekt.cli.console.red
import io.gitlab.arturbosch.detekt.cli.createClasspath
import io.gitlab.arturbosch.detekt.cli.createFilters
import io.gitlab.arturbosch.detekt.cli.createPlugins
import io.gitlab.arturbosch.detekt.cli.extractUris
import io.gitlab.arturbosch.detekt.cli.getOrComputeWeightedAmountOfIssues
import io.gitlab.arturbosch.detekt.cli.isValidAndSmallerOrEqual
import io.gitlab.arturbosch.detekt.cli.loadConfiguration
import io.gitlab.arturbosch.detekt.cli.maxIssues
import io.gitlab.arturbosch.detekt.core.DetektFacade
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import java.io.PrintStream

class Runner(
    private val arguments: CliArgs,
    private val outputPrinter: PrintStream,
    private val errorPrinter: PrintStream
) : Executable {

    override fun execute() {
        createSettings().use { settings ->
            val (checkConfigTime) = measure { checkConfiguration(settings) }
            settings.debug { "Checking config took $checkConfigTime ms" }
            val (serviceLoadingTime, facade) = measure { DetektFacade.create(settings) }
            settings.debug { "Loading services took $serviceLoadingTime ms" }
            var (engineRunTime, result) = measure { facade.run() }
            settings.debug { "Running core engine took $engineRunTime ms" }
            checkBaselineCreation(result)
            result = transformResult(result)
            val (outputResultsTime) = measure { OutputFacade(arguments, result, settings).run() }
            settings.debug { "Writing results took $outputResultsTime ms" }
            if (!arguments.createBaseline) {
                checkBuildFailureThreshold(result, settings)
            }
        }
    }

    private fun transformResult(result: Detektion): Detektion {
        val baselineFile = arguments.baseline
        return if (baselineFile != null) {
            FilteredDetectionResult(result, BaselineFacade(baselineFile))
        } else {
            result
        }
    }

    private fun checkBaselineCreation(result: Detektion) {
        if (arguments.createBaseline) {
            val smells = result.findings.flatMap { it.value }
            BaselineFacade(arguments.baseline!!).create(smells)
        }
    }

    private fun checkBuildFailureThreshold(result: Detektion, settings: ProcessingSettings) {
        val amount = result.getOrComputeWeightedAmountOfIssues(settings.config)
        val maxIssues = settings.config.maxIssues()
        if (maxIssues.isValidAndSmallerOrEqual(amount)) {
            throw BuildFailure("Build failed with $amount weighted issues (threshold defined was $maxIssues).".red())
        }
    }

    private inline fun <T> measure(block: () -> T): Pair<Long, T> {
        val start = System.currentTimeMillis()
        val result = block()
        return System.currentTimeMillis() - start to result
    }

    private fun createSettings(): ProcessingSettings = with(arguments) {
        val (configLoadTime, configuration) = measure { loadConfiguration() }
        val (settingsLoadTime, settings) = measure {
            ProcessingSettings(
                inputPaths = inputPaths,
                config = configuration,
                pathFilters = createFilters(),
                parallelCompilation = parallel,
                autoCorrect = autoCorrect,
                excludeDefaultRuleSets = disableDefaultRuleSets,
                pluginPaths = createPlugins(),
                classpath = createClasspath(),
                languageVersion = languageVersion,
                jvmTarget = jvmTarget,
                debug = arguments.debug,
                outPrinter = outputPrinter,
                errPrinter = errorPrinter,
                configUris = extractUris()
            )
        }
        settings.debug { "Loading config took $configLoadTime ms" }
        settings.debug { "Creating settings took $settingsLoadTime ms" }
        return settings
    }
}
