package io.gitlab.arturbosch.detekt.cli.runners

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.internal.SimpleNotification
import io.gitlab.arturbosch.detekt.cli.BuildFailure
import io.gitlab.arturbosch.detekt.cli.CliArgs
import io.gitlab.arturbosch.detekt.cli.OutputFacade
import io.gitlab.arturbosch.detekt.cli.createClasspath
import io.gitlab.arturbosch.detekt.cli.createFilters
import io.gitlab.arturbosch.detekt.cli.createPlugins
import io.gitlab.arturbosch.detekt.cli.getOrComputeWeightedAmountOfIssues
import io.gitlab.arturbosch.detekt.cli.isValidAndSmallerOrEqual
import io.gitlab.arturbosch.detekt.cli.loadConfiguration
import io.gitlab.arturbosch.detekt.cli.maxIssues
import io.gitlab.arturbosch.detekt.core.DetektFacade
import io.gitlab.arturbosch.detekt.core.ProcessingSettings

class Runner(private val arguments: CliArgs) : Executable {

    override fun execute() {
        createSettings().use { settings ->
            val (time, result) = measure { DetektFacade.create(settings).run() }
            result.add(SimpleNotification("detekt finished in $time ms."))
            OutputFacade(arguments, result, settings).run()
            if (!arguments.createBaseline) {
                checkBuildFailureThreshold(result, settings)
            }
        }
    }

    private fun checkBuildFailureThreshold(result: Detektion, settings: ProcessingSettings) {
        val amount = result.getOrComputeWeightedAmountOfIssues(settings.config)
        val maxIssues = settings.config.maxIssues()
        if (maxIssues.isValidAndSmallerOrEqual(amount)) {
            throw BuildFailure("Build failed with $amount weighted issues (threshold defined was $maxIssues).")
        }
    }

    inline fun <T> measure(block: () -> T): Pair<Long, T> {
        val start = System.currentTimeMillis()
        val result = block()
        return System.currentTimeMillis() - start to result
    }

    private fun createSettings(): ProcessingSettings = with(arguments) {
        ProcessingSettings(
            inputPaths = inputPaths,
            config = loadConfiguration(),
            pathFilters = createFilters(),
            parallelCompilation = parallel,
            autoCorrect = autoCorrect,
            excludeDefaultRuleSets = disableDefaultRuleSets,
            pluginPaths = createPlugins(),
            classpath = createClasspath(),
            languageVersion = languageVersion,
            jvmTarget = jvmTarget,
            debug = arguments.debug
        )
    }
}
